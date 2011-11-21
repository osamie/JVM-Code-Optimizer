
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Vector;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;

public class JasminOptimizer {

    Vector<CodeBlock> codeBlocks;

    public static void main(String args[]) {
        JasminOptimizer jopt = null;
        try {
            jopt = new JasminOptimizer(System.in);
        } catch (IOException e) {
            System.err.println("Error: An exception occured: " 
                               + e.getMessage());
        }
        jopt.optimize();
        jopt.outputAll(System.out);
    }

    protected void init() {
        // initialize the list of code blocks
        codeBlocks = new Vector<CodeBlock>();
    }

    /* Create a new JasminOptimizer for the Jasmin instructions
     * given in is.
     */
    public JasminOptimizer(InputStream is) throws IOException {
        init();
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        parseInput(r);
    }

    protected void parseInput(BufferedReader r) throws IOException {        
        // This maps label names onto the code blocks in which they appear
        Hashtable<String,CodeBlock> labels = new Hashtable<String,CodeBlock>();
        CodeBlock b = new CodeBlock();
        while (r.ready()) {
            String line = r.readLine();
            StringTokenizer st = new StringTokenizer(line);
            // FIXME: This has a problem with string constants
            // containing whitespace
            Vector<String> v = new Vector<String>();
            for (int i = 0; st.hasMoreTokens(); i++) {                
                String t = st.nextToken();
                if (t.charAt(0) == ';') break;
                v.add(t);
            }
            if (v.size() > 0) {
                String first = v.elementAt(0);
                if (isMethodDirective(first)) {
                    // A new method, start a new block
                    CodeBlock b2 = new CodeBlock();
                    if (b.size() > 0 || b.getLabel() != null) {
                        codeBlocks.add(b);
                    }
                    b = b2;
                    b.addLine(v);
                } else if (isDirective(first)) {
		    // A directive, just add it to the current block
                    b.addLine(v);
                } else if (isLabel(first)) {
		    if (b.size() > 0 || b.getLabel() != null) {
                        // start a new block
			CodeBlock b2 = new CodeBlock();
			b2.setLabel(first);
                        if (labels.containsKey(first)) {
                          System.err.println("Error: Label \"" + first 
                            + "\" is used more than once"
                            + " - please make all labels unique");
                          System.exit(-1);
                        }
			labels.put(first, b2);
			b.addSuccessor(b2);
			codeBlocks.add(b);
			b = b2;
		    } else {
                        // just set the label of the current block
			b.setLabel(first);
			labels.put(first, b);
                    }
                } else if (isGoto(first)) {
                    // An unconditional jump instruction
                    b.addLine(v);
                    CodeBlock b2 = new CodeBlock();
                    if (b.size() > 0 || b.getLabel() != null) {
                        codeBlocks.add(b);
                    }
                    b = b2;
                } else if (isConditionalJump(first)) {
                    // A conditional jump instruction
                    b.addLine(v);
                    CodeBlock b2 = new CodeBlock();
                    b.addSuccessor(b2);
                    if (b.size() > 0 || b.getLabel() != null) {
                        codeBlocks.add(b);
                    }
                    b = b2;
                } else if (isReturn(first)) {
                    // This is a return statement
                    b.addLine(v);
                    CodeBlock b2 = new CodeBlock();
                    if (b.size() > 0 || b.getLabel() != null) {
                        codeBlocks.add(b);
                    }
                    b = b2;
                } else {
                    // A normal instruction
                    b.addLine(v);
                }
            }
        }
        codeBlocks.add(b);

        // Next we make a pass through the code blocks to resolve the
        // locations of jump instructions
        for (int i = 0; i < codeBlocks.size(); i++) {
            // get the last instruction in this block
            b = codeBlocks.elementAt(i);
            LinkedList lines = b.getLines();
            if (lines.size() > 0) {
                Vector line = (Vector)lines.getLast();
                String instruction = (String)line.elementAt(0);
                if (isGoto(instruction) || isConditionalJump(instruction)) {
                    String location = (String)line.elementAt(1);
                    String key = location + ":";
                    if (labels.containsKey(key)) {
                        CodeBlock b2 = labels.get(key);
                        b.addSuccessor(b2);
                    } else {
                        System.err.println("Warning: unrecognized label " 
                                           + location
                                           + " used in " + instruction + 
                                           " instruction");
                    }
                }
            }
        }
    }

    /* This is the general purpose optimization routine
     */
    public void optimize() {
        for (int i = 0; i < codeBlocks.size(); i++) {
            CodeBlock b = codeBlocks.elementAt(i);
            optimizeBlock(b);
        }
    }
    
    /* This is the code for optimizing an individual CodeBlock
     */
    protected void optimizeBlock(CodeBlock b) {
        LinkedList<Vector<String>> lines = b.getLines();
        ListIterator<Vector<String>> i = lines.listIterator(); 
        while (i.hasNext()) {
            Vector line = (Vector)i.next();
            String ins = (String)line.elementAt(0);
            if (isLoadInstruction(ins) && i.hasNext()) {
                Vector nextLine = (Vector)i.next();
                String nextIns = (String)nextLine.elementAt(0);
                if (isPopInstruction(nextIns)) {
                    // This line and the previous one are useless, remove them
                    i.remove();
                    i.previous();
                    i.remove();
                    Vector<String> v = new Vector<String>(1);
                    v.add("; Removed useless load/pop pair: " 
                          + line + nextLine);
                    i.add(v);
                }
            } 
        } 
    }

    /* Output this Jasmin code onto the given output stream
     */
    public void outputAll(PrintStream os) {
        for (int i = 0; i < codeBlocks.size(); i++) {
            os.println("  ;;;;;;;;; Begin Block " + i + " ;;;;;;;;;");
            CodeBlock b = codeBlocks.elementAt(i);
            String label = b.getLabel();
            if (label != null && label.length() > 1) {
		os.println(label + " ; label");
            }
            LinkedList lines = b.getLines();
            for (ListIterator j = lines.listIterator(); j.hasNext(); ) {
                Vector line = (Vector)j.next();
                String s = "";
                for (int k = 0; k < line.size(); k++) {
                    s += (String)line.elementAt(k) + " ";
                }
                if (!isDirective(s)) {
		    os.print("  ");
                }
                os.println(s);
            }
            for (int j = 0; j < b.getSuccessors().size(); j++) {
                if (j == 0) {
		    os.print("  ; Block " + i + " exits to blocks");
                }
                CodeBlock b2 = b.getSuccessors().elementAt(j);
                int bn = codeBlocks.indexOf(b2);
                os.print(" " + bn);
                if (j == b.getSuccessors().size() - 1) {
		    os.println("");
                }
            }
            os.println("  ;;;;;;;;; End Block " + i + " ;;;;;;;;;");
        }
    }


    /* Return true iff the given string is a Jasmin directive
     */
    protected static boolean isDirective(String s) {
        return s.charAt(0) == '.';
    }

    /* Return true if this is a Jasmin .method directive
     */
    protected static boolean isMethodDirective(String s) {
        return s.compareTo(".method") == 0;
    }

    /* Return true iff the given string is a Jasmin label
     */
    protected static boolean isLabel(String s) {
        return s.charAt(s.length()-1) == ':'; 
    }
    
    /* Return true iff the given string a Jasmin goto instruction
     */
    protected static boolean isGoto(String s) {
        return s.compareTo("goto") == 0;
    }

    /* Return true iff the given string is a Jasmin pop instruction
     */
    protected static boolean isPopInstruction(String s) {
        return s.compareTo("pop") == 0;
    }
    
    /* Return true iff the given string is a Jasmin load instruction
     */
    protected static boolean isLoadInstruction(String s) {
        // FIXME: incomplete list
        String loadInstructions[] = { "iload", "aload", "fload",
				      "aaload", "baload", "caload",
				      "daload", "faload", "fload", "iaload",
				      "ldc" };
        for (int i = 0; i < loadInstructions.length; i++) {
            if (s.compareTo(loadInstructions[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    /* Return true iff the given string is a jump instruction
     */
    protected static boolean isJump(String s) {
	return isConditionalJump(s) || isUnconditionalJump(s);
    }

    /* Return true iff the given string is an unconditional jump instruction
     */
    protected static boolean isUnconditionalJump(String s) {
        String jumpInstructions[] = { "goto", "goto_w", "jsr", "jsr_w" };
        for (int i = 0; i < jumpInstructions.length; i++) {
            if (s.compareTo(jumpInstructions[i]) == 0) {
                return true;
            }
        }
        return false;
    }
        
    /* Return true iff the given string is a conditional jump instruction
     */
    protected static boolean isConditionalJump(String s) {
        String jumpInstructions[] = {
            "iflt", "ifle", "ifeq", "ifne", "ifgt", "ifge",
            "ifnonnull", "ifnull", "if_acmpeq", "ifacmpne",
            "if_icmpeq", "if_icmpge", "if_icmpgt", "if_icmple", "if_icmplt",
            "if_icmpne" };

        for (int i = 0; i < jumpInstructions.length; i++) {
            if (s.compareTo(jumpInstructions[i]) == 0) {
                return true;
            }
        }
        return false;
    }

    /* Return true iff the given string is a return instruction
     */
    protected static boolean isReturn(String s) {
        String returnInstructions[] = {
            "return", "ireturn", "areturn", "freturn", "dreturn", "lreturn"
        };
        for (int i = 0; i < returnInstructions.length; i++) {
            if (s.compareTo(returnInstructions[i]) == 0) {
                return true;
            }
        }
        return false;        
    }
}
