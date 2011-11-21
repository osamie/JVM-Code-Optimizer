
import java.util.Vector;
import java.util.LinkedList;

/* A CodeBlock represents a basic block of code that always executes
 * straight-through. The code itself is represented as a LinkedList,
 * each of whose entries represent a line of code.  A CodeBlock also
 * has a list of successors. These are other CodeBlocks that may be
 * executed immediately after the completion of this CodeBlock.
 */
public class CodeBlock {
    /* A list of blocks that may be executed after this block
     */
    Vector<CodeBlock> successors;
    
    /* The lines of code that make up this block 
     */
    LinkedList<Vector<String>> lines;

    /* The label (if any) associated with the head of this block
     */
    String label;

    /* Create a new empty code block with no successors or lines of
     * code.
     */
    public CodeBlock() {
	successors = new Vector<CodeBlock>();
	lines = new LinkedList<Vector<String>>();
    }


    /* Set this block's label
     */
    public void setLabel(String l) {
        label = l;
    }

    /* Get this block's label
     */
    public String getLabel() {
        return label;
    }

    /* Add a line of code to this code block. A line of code is
     * represented as a Vector of strings.
     */
    public void addLine(Vector<String> v) {
	lines.add(v);
    }
    
    /* Add a successor to this block's list of successors.
     */
    public void addSuccessor(CodeBlock b) {
	successors.add(b);
    }

    /* Return the size (number of lines of code) of this block
     */
    public int size() {
	return lines.size();
    }

    /* Return this block's list of lines of code
     */
    public LinkedList<Vector<String>> getLines() {
	return lines;
    }

    /* Return this block's list of successors
     */
    public Vector<CodeBlock> getSuccessors() {
	return successors;
    }
}
