
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;

public class JasminOptimizer{

    Vector<CodeBlock> codeBlocks;
    Vector<CodeBlock> assessedBlocks;

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
                } 
                else if (isDirective(first)) {
		    // A directive, just add it to the current block
                    b.addLine(v);
                } 
                else if (isLabel(first)) {
                	if (b.size() > 0 || b.getLabel() != null) {
                		
                		//before starting a new block check if this label is a 
                		//target of a jump instruction
                		
                		
                		
                		
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
                	} 
                	else {
                        // just set the label of the current block
						b.setLabel(first);
						labels.put(first, b);
                    }
                } 
                else if (isGoto(first)) {
                    // An unconditional jump instruction
                    b.addLine(v);
                    CodeBlock b2 = new CodeBlock();
                    if (b.size() > 0 || b.getLabel() != null) {
                        codeBlocks.add(b);
                    }
                    b = b2;
                } 
                else if (isConditionalJump(first)) {
                    // A conditional jump instruction
                    b.addLine(v);
                    CodeBlock b2 = new CodeBlock();
                    b.addSuccessor(b2);
                    if (b.size() > 0 || b.getLabel() != null) {
                        codeBlocks.add(b);
                    }
                    b = b2;
                } 
                else if (isReturn(first)) {
                    // This is a return statement
                    b.addLine(v);
                    CodeBlock b2 = new CodeBlock();
                    if (b.size() > 0 || b.getLabel() != null) {
                        codeBlocks.add(b);
                    }
                    b = b2;
                } 
                else {
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
        	optimizeBLOCK_LoadPopPairs(b);
            
            optimize_PrecalculateArithmeticConstants(b);
            
            
        }
        
        optimizeJumpsToUnconditionalJumps();

    }
    
protected void optimize_EliminateUselessStore() {
		
 		
		for (int i = 0; i < codeBlocks.size() - 1; i++) {
			CodeBlock b = codeBlocks.elementAt(i);
			LinkedList<Vector<String>> lines = b.getLines();
			
			for(int j = 0; j<lines.size();j++){
				if(isStore(lines.get(j).elementAt(0))){
					
					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						if(leadsStoreToDeadEnd(j, b)){
							System.out.println(lines.get(j).elementAt(0));
							//lines.remove(j);
							
							lines.get(j).setElementAt("pop", 0);
							lines.get(j).add("; Removed store that leads to dead end --- QUESTION 7");
							
							//System.out.println(lines.get(j).elementAt(0));
							//lines.get(j).add("; Removed store that leads to dead end --- QUESTION 7 \n");
							
							
							/*Vector<String> v = new Vector<String>();
                            v.add("pop");
                            v \n");
                            lines.add(v);
                            ++j;*/
						}
				}
			}
			
		}
	}
    
    
    protected void optimizeBLOCK_LoadPopPairs(CodeBlock b) {
		LinkedList<Vector<String>> lines = b.getLines();
		ListIterator<Vector<String>> i = lines.listIterator();
		while (i.hasNext()) {
			Vector line = (Vector) i.next();
			String ins = (String) line.elementAt(0);
			
				if (isPopInstruction(ins)) {
					
					deletePrevious2(i); // the pop and the popped = 2
					Vector<String> v = new Vector<String>(1);
					
					v.add("; useless load/pop pair removed (including useless instructions in between)" + line);
					i.add(v);
				}
		}
	}
    protected void findRemovableBlocks(CodeBlock b){
		
		if(assessedBlocks.contains(b)) return;
		
		assessedBlocks.addElement(b);
		
		
		Vector<CodeBlock> successors = b.getSuccessors();
		
		for(int i =0;i<successors.size();i++){
			findRemovableBlocks(successors.get(i));
		}
    }
    
    protected void optimize_RemoveNonVisitedBlocks() {
    		assessedBlocks = new Vector<CodeBlock>();
    		
    		findRemovableBlocks(codeBlocks.get(0));
    		
    	
    		for (int i = 0; i < codeBlocks.size(); i++) {
    			CodeBlock b = codeBlocks.elementAt(i);
    			LinkedList<Vector<String>> lines = b.getLines();
    			
    			for(int j = 0; j<lines.size();j++){
    				if(isDirective(lines.get(j).elementAt(0))){
    					
    					if(!assessedBlocks.contains(b)){
    						findRemovableBlocks(b);
    					}
    				}
    			}
    			
    		}
    		
    		codeBlocks = assessedBlocks;
    	}
    
    protected void optimize_PrecalculateArithmeticConstants(CodeBlock b) {
        LinkedList<Vector<String>> lines = b.getLines();
        ListIterator<Vector<String>> i = lines.listIterator(); 
    
        if(i.hasNext()) i.next();
        if(i.hasNext()) i.next();
        
        while (i.hasNext()) {
            Vector line = (Vector)i.next();
            String ins = (String)line.elementAt(0);
        	
            
            if (ins.equals("fmul") || ins.equals("fadd") || ins.equals("fsub") || ins.equals("fdiv")) 
            {
            	i.previous();
            	line = (Vector)i.previous();
            	if (((String)line.elementAt(0)).equals("ldc")) {
            		
            		if(isFLoat((String)line.elementAt(1))){
            			
            			float x = getFloatFromString((String)line.elementAt(1));
            			
            			line = (Vector)i.previous();
                    	
            			if (((String)line.elementAt(0)).equals("ldc")) {
                    		if(isFLoat((String)line.elementAt(1))){     			
                    			float y = getFloatFromString((String)line.elementAt(1));
                    			
                    			line = (Vector)i.next();
                    			
                    			line = (Vector)i.next();
                    			
                    			line = (Vector)i.next();
                    			
                    			float m=0;
                    			if (ins.equals("fmul")) m = x*y;
                    			else if (ins.equals("fdiv")) m = x/y;
                    			else if (ins.equals("fadd")) m = x+y;
                    			else if (ins.equals("fsub")) m = x-y;
                    			
                    			//build the line to be added 
                				Vector<String> v = new Vector<String>(1);
                                v.add("ldc");
                                v.add("" + m);
                                
                                String outp = " ";
                                if (ins.equals("fmul"))  outp = x + " * " + y;
                    			else if (ins.equals("fdiv")) outp = x + " / " + y;
                    			else if (ins.equals("fadd")) outp = x + " + " + y;
                    			else if (ins.equals("fsub")) outp = x + " - " + y;
                                
                                v.add("; precalculated arithmetic constant ( " + outp + ")"   );
                                
                                i.remove();
                                i.previous();
                                i.remove();
                                i.previous();
                                i.remove();
                                
                                
                                i.add(v);
                               
                                continue;
                    		}
                    	}
                    	i.next();
            		}
            		
            	}
            	
            	i.next();
            	i.next();
            }
        }
    }
    
    
protected void optimizeJumpsToUnconditionalJumps() {
		
		HashMap gotoHashMap = new HashMap();
	
		for (int i = 0; i < codeBlocks.size() - 1; i++) {
			CodeBlock b = codeBlocks.elementAt(i);
			LinkedList<Vector<String>> lines = b.getLines();
			
				if(isGoto(lines.get(0).elementAt(0))){
					//System.out.println(" --------------- goto");
					
					gotoHashMap.put(b.getLabel().substring(0,b.getLabel().length()-1), lines.get(0).elementAt(1));
					
					System.out.println(b.getLabel().substring(0,b.getLabel().length()-1)+" , " + lines.get(0).elementAt(1));
				}
			
		}
		
		ArrayList<Vector<String>> jumpLinesList = new ArrayList<Vector<String>> ();
		ArrayList<String> jumpsKeyList = new ArrayList<String>();
		
		
		for (int i = 0; i < codeBlocks.size() - 1; i++) {
			CodeBlock b = codeBlocks.elementAt(i);
			LinkedList<Vector<String>> lines = b.getLines();
			
			for(int j = 0; j<lines.size();j++){
				if(isJump(lines.get(j).elementAt(0))){
					if(gotoHashMap.containsKey(lines.get(j).elementAt(1))){
						
						
						
							//System.out.println(" --------------- jump");
							
							jumpLinesList.add(lines.get(j));
							jumpsKeyList.add(lines.get(j).elementAt(1));
							System.out.println(lines.get(j).elementAt(1));
						//}
					}
				}
			}
		}
		
		boolean checkAgain = true;
		
		while(checkAgain){
			checkAgain = false;
			
		
			for(int p=0;p<jumpsKeyList.size();p++){
				if(gotoHashMap.containsKey(jumpsKeyList.get(p))){
					
					//System.out.println(" --------------- changing the go to label  !");
					
					checkAgain = true;
					String theGotoUnderJumpLabel = (String)gotoHashMap.get(jumpsKeyList.get(p));
					// get the line that corresponds to jump statement
					Vector<String> line = (Vector<String>)jumpLinesList.get(p);
					
					// set the element to the new jump (optimized)
					
					
					line.add(";Optimized jump to unconditinal jump. " + line.elementAt(1) + " to : " +  theGotoUnderJumpLabel+"removing " + jumpsKeyList.get(p) +"\n");
					line.setElementAt(theGotoUnderJumpLabel,1);
					//jumpsHashMap.remove(jumpsKeyList.get(p));
					
					
					
					// remove the jump from the list since it has been replaced
					jumpsKeyList.remove(p);
					jumpLinesList.remove(p);
					--p;
					
					// add the new one.
					jumpLinesList.add(line);
					jumpsKeyList.add(theGotoUnderJumpLabel);
					
				}
				
			}
			
		}
		
		
	}
    
    
    boolean isFLoat(String s) {

		try {
			float f = Float.valueOf(s.trim()).floatValue();

		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
    
    float getFloatFromString(String s) {
		return Float.valueOf(s.trim()).floatValue();
	}
    
    
    
    
            
            
            
            /*
        	 * if 
        	 * -first line of block is a label
        	 * -current block is not target of a jump
        	 * -last line in the previous block is not a jump (conditional or unconditional)
        	 * 
        	 * THEN
        	 * 
        	 * -remove label or the first line of current block 
        	 * -Merge the block to the previous block
        	 * -delete current block 
        	 */
           
            //isLabel(b.getLines().getFirst().elementAt(0))
            /**if (b.getLabel()!=null && (i>1) ) //not sure of check here
            {
            	 
            	
            	 
            	CodeBlock b_prev = codeBlocks.elementAt(i-1);
            	
            	
            		
            	
            	if (!istargetOf_a_Jump(b) && !isJump(b_prev.getLines().getLast().elementAt(0))){
            		
            		//throw new RuntimeException("current label '" + b.getLabel() + "' " + " previous block: " + b_prev.getLabel());	
            		//throw new RuntimeException("HERE! " + b.getLabel() + " . b_prev's last: " + b_prev.getLines().getLast().elementAt(0) );	
            		
            		
            		for (int j = 0; j < b.getLines().size();j++)
            		{
            			
            			b_prev.addLine(b.getLines().get(j));
            			
            			
            		}
            		//throw new RuntimeException("HERE!  . b_prev's last: " + b_prev.getLines().getLast().toString() + "b's size is:" + b.getLines().size() );
            		           		
            		b_prev.successors.remove(b);
            		codeBlocks.remove(b);
            		b = b_prev;
            		i-=1;
            		
            		
            	}
            	
            	**/
            	
            	
            	
           
    
    /* This is the code for optimizing an individual CodeBlock
     */
    protected void optimizeBlock(CodeBlock b) {
        LinkedList<Vector<String>> lines = b.getLines();
        ListIterator<Vector<String>> i = lines.listIterator(); 
        ListIterator<Vector<String>> k = lines.listIterator();
        while (i.hasNext()) {
        	
        	
            Vector line = (Vector)i.next();
            String ins = (String)line.elementAt(0);  
            
            
            //removing useless load/pop pair
            if (isLoadInstruction(ins) && i.hasNext()) {
            	//throw new RuntimeException("block is "  + b.getLines() + "\n curent line is " + (line) + "another: " + i.next());
                Vector nextLine = (Vector)i.next();
                String nextIns = (String)nextLine.elementAt(0);
                
                
                k=i;
                
                if (isPopInstruction(nextIns)) {
                    // This line and the previous one are useless, remove them
                    i.remove();  //removing pop 
                    i.previous(); 
                    i.remove(); //removing load instruction
                    Vector<String> v = new Vector<String>(1);
                    v.add("; Removed useless load/pop pair: " 
                          + line + nextLine);
                    i.add(v);
                    
                    //throw new RuntimeException("block is \n curent line is " + (line));
                }
                
                
                
        
                /**
                 * if (isLoadinstruction(cl_ins) && isLoadinstruction(nl_ins) )
             * {
             * 		sec_line
             * 		third_line
             * 
             * 		if (isMultiplication(3rd_line)
             * 		if (cl_ins_1 == (2.0||2)))
             * 		{
             * 			-remove line (Current_line = next_line)
             * 			-next_line becomes "dup"
             * 		}
             * 
             *  	else if(nl_ins_1 == (2.0||2))
             *      {
             *      	change next line to dup
             *      }
             * 		
             * }  
                 */
                
                if(i.hasNext()){
                	Vector thirdLine = (Vector)i.next();
                
                	String thirdIns = (String)thirdLine.elementAt(0);
                
                
                if(isLoadInstruction(nextIns) && isMultiplication(thirdIns)){
                    
                    String line1_load_operand = (String)line.elementAt(1);
                    String line2_load_operand = (String)nextLine.elementAt(1);
                    
                    
                    	if (line1_load_operand.equals("2")||line1_load_operand.equals("2.0")){
                    		/**
                    		 * example 
                    		 * fload 2
                    		 * ldc 3
                    		 * fmul
                    		 * 
                    		 */
                            		
                    		line.removeAllElements();
                    		line.addAll(nextLine);
                    		nextLine.removeAllElements();
                    		nextLine.add(new String("dup"));
                    		thirdLine.removeAllElements();
                    		thirdLine.add(new String("fadd"));
                    		
                    		 //should be able to do fadd or iadd or ladd or dadd
                    		
                    		//throw new RuntimeException("HERE! first line " + line1_load_operand);
                    	}
                    	
                    	else if ((line2_load_operand.equals("2"))||(line2_load_operand.equals("2.0"))) {
                    		nextLine.removeAllElements();
                    		nextLine.add(new String("dup"));
                    		
                    		thirdLine.removeAllElements();
                    		
                    		thirdLine.add(new String("fadd"));
                    		
                    		
                    		
                    		//throw new RuntimeException("HERE 2!" + thirdIns);
                    	} 
                    	
                    	
                    
                    i = k;
                	
                }
                
                
            }          
      
            //replace multiplication by 2 with a single addition
            /**
             * fload 3
             * ldc 2.0
             * fmul
             * 
             * 
             * fload 3
             * dup
             * fadd
             * 
             * Current_line
             * cl_ins = Current_line(firstWORD)
             * cl_ins_1 = CUrrent_line(constant) 
             * 
             * next_line
             * nl_ins = next_line(firstWORD)
             * nl_ins_1 = next_line(constant)
             * 
             * 3rd_line
             * 3l_ins = 3rd_line(firstWORD)
             * 
             * if (isLoadinstruction(cl_ins) && isLoadinstruction(nl_ins) )
             * {
             * 		sec_line
             * 		third_line
             * 
             * 		if (isMultiplication(3rd_line)
             * 		if (cl_ins_1 == (2.0||2)))
             * 		{
             * 			-remove line (Current_line = next_line)
             * 			-next_line becomes "dup"
             * 		}
             * 
             *  	else if(nl_ins_1 == (2.0||2))
             *      {
             *      	change next line to dup
             *      }
             * 		
             * }  
             * 
             */
            
            
            
        } //end of while loop
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
            	if (istargetOf_a_Jump(b)){
            		os.println(label + " ;label (target of jump) ");
            	}
            	else os.println(label + " ; label");
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
    
    /*
     * Return true if the given string is a label and target of a jump
     * 
     */
    protected boolean istargetOf_a_Jump(CodeBlock b){
    	
    	//check if the given block is a label
    	String s = b.getLabel();
    	//String s = b.lines.getFirst().firstElement();
    	/**if(!isLabel(s)){
    		return false;
    	}**/
    	//int count = 0;
    	
    	for (int i = 0; i < codeBlocks.size(); i++) {
            CodeBlock k = codeBlocks.elementAt(i);
            Vector<CodeBlock> cb = k.getSuccessors();
                       
          //checking if current codeblock k is a predecessor of block b
            //if it's a predecessor, then check if it's a jump statement
            if ((cb.contains(b))&&isJump(k.getLines().getLast().elementAt(0))) 
            {
            	String jmp_target = k.getLines().getLast().elementAt(1) + ":";
            	if((jmp_target).equals(b.getLabel())) {
            		//throw new RuntimeException("HERER " + k.getLines().getLast().elementAt(1));
            		//throw new RuntimeException("current block is "+ b.getLabel() + "\n" + "  Target: " + k.getLines().getLast().elementAt(1));
            		return true;
            	}
            		           	
            }
            //optimizeBlock(b);	
        }
    	
    	return false;
    	
    	
    }
    
    /* Return true if the given string is a multiplication instruction
     */
    protected static boolean isMultiplication(String s) {
        String returnInstructions[] = {"imul", "dmul", "lmul", "fmul"};
        
        for (int i = 0; i < returnInstructions.length; i++) {
            if (s.compareTo(returnInstructions[i]) == 0) {
                return true;
            }
        }
        return false;        
    }
    
    protected boolean isRemovableONEaboveString(String s){
		
		String instructions[] = { "fload", "ldc" , "pop"};
		for (int i = 0; i < instructions.length; i++) {
			if (s.compareTo(instructions[i]) == 0) {
				return true;
			}
		}
		return false;
	}
protected boolean isRemovableTWOaboveString(String s){
	
	String instructions[] = { "fmul", "dmul", "imul", "lmul", "fadd", "dadd", "iadd", "ladd", "fdiv", "ddiv", "idiv", "ldiv", "fsub", "dsub", "isub", "lsub" };
	for (int i = 0; i < instructions.length; i++) {
		if (s.compareTo(instructions[i]) == 0) {
			return true;
		}
	}
	return false;
}


protected void deletePrevious2(ListIterator<Vector<String>> i) {
	delete_previous(i);
	delete_previous(i);
}
protected void delete_previous(ListIterator<Vector<String>> i) {
	
	i.remove();
	if(!i.hasPrevious()) return;
	i.previous();
	Vector line = (Vector) i.next();
	String ins = (String) line.elementAt(0);
	
	if(isRemovableONEaboveString(ins)){
		delete_previous(i);
	}else if(isRemovableTWOaboveString(ins)){
		deletePrevious2(i);
	}else{
		return;
	}
}

protected boolean isStore(String s) {
	String loadInstructions[] = { "istore", "astore", "fstore"};
	for (int i = 0; i < loadInstructions.length; i++) {
		if (s.compareTo(loadInstructions[i]) == 0) {
			return true;
		}
	}
	return false;
}

protected static boolean isLoad(String s) {
	// FIXME: incomplete list
	String loadInstructions[] = { "iload", "aload", "fload", "aaload",
			"baload", "caload", "daload", "faload", "fload", "iaload"};
	for (int i = 0; i < loadInstructions.length; i++) {
		if (s.compareTo(loadInstructions[i]) == 0) {
			return true;
		}
	}
	return false;
}

protected boolean hasALoad(CodeBlock b, int currentStoreCount){
	LinkedList<Vector<String>> lines = b.getLines();
	
	for(int j = 0; j<lines.size();++j){
		if(isLoad(lines.get(j).elementAt(0))){
			if(currentStoreCount == 0)	// no other stores met so load found and no dead end has been reached
				return true;
			else
				--currentStoreCount;
		}else if(isStore(lines.get(j).elementAt(0))){
			++currentStoreCount;
		}
	}
	
	Vector<CodeBlock> successors = b.getSuccessors();
	for(int i=0;i<successors.size();i++){
	 if(hasALoad(successors.get(i), currentStoreCount)){
		 return true;
	 }
	}
	return false;
	
}

protected boolean leadsStoreToDeadEnd(int currentLineIndex, CodeBlock b){
	LinkedList<Vector<String>> lines = b.getLines();

	int currentStoreCount = 0;
	for(int j = currentLineIndex; j<lines.size();++j){
		if(isLoad(lines.get(j).elementAt(0))){
			if(currentStoreCount == 0)	{// no other stores met so load found and no dead end has been reached
				//System.out.println("RETURNED" + lines.get(j).elementAt(0));
				return false;
			}else
				--currentStoreCount;
		}else if(isStore(lines.get(j).elementAt(0))){
			++currentStoreCount;
		}
	}

	// now go through all the reachable blocks. if at least one has a load then 
	// we cannot assume that we always reach a dead end ! - return false
	Vector<CodeBlock> successors = b.getSuccessors();
	
	for(int i=0;i<successors.size();i++){
	 if(hasALoad(successors.get(i), currentStoreCount)){
		 return false;
	 }
	}
	
	return true;
	
}


    
    
}
