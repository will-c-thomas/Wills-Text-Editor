import javax.swing.text.*;
import java.util.*;
import java.lang.*;

class BrainFkInterpreter {
	
	String code;

	public BrainFkInterpreter(String code) {
		this.code = code;
	}

	public BrainFkInterpreter(JTextComponent txtComp) {
		this(txtComp.getText());
	}

	public void setText(String code) {
		this.code = code;
	}

	public void setText(JTextComponent txtComp) {
		this.setText(txtComp.getText());
	}

	public String getText() {
		return code;
	}

	public String interpret() {

		//create memory
		ArrayList<Integer> memory = new ArrayList<Integer>();
		memory.add(0);
		int memPos = 0;

		//check if there is code
		if(code.length() < 1)
			return "No stored code.";

		//run for-loop to do code
		for(int codePos = 0; codePos < code.length();) {
			char curr = code.charAt(codePos);

			if(curr == '+') { //increment

				codePos++;
				memory.set(memPos, memory.get(memPos) + 1);

			} else if(curr == '-') { //decrement

				codePos++;
				memory.set(memPos, memory.get(memPos) - 1);

			} else if(curr == '>') { //move to the right

				codePos++;
				memPos++;

				if(memPos >= memory.size())
					memory.add(0);

			} else if(curr == '<') { //move to the left

				codePos++;
				memPos--;

				if(memPos < 0)
					return "Invalid memory range.";

			} else if(curr == '[') { //start loop

				codePos++;

			} else if(curr == ']') { //end loop

				if(memory.get(memPos) == 0) {
					codePos++;
				} else {
					int bracketCounter = 1;

					while(bracketCounter != 0) {

						codePos--;

						if(codePos < 0 || bracketCounter < 0)
							return "Loop error. Check for missing brackets.";

						if(code.charAt(codePos) == ']')
							bracketCounter++;
						else if(code.charAt(codePos) == '[')
							bracketCounter--;

					}

				}

			} else if(curr == '.') { //print current value

				codePos++;
				System.out.println(Character.toChars(memory.get(memPos)));

			} else if(curr == ',') { //input value

				codePos++;
				Scanner sc = new Scanner(System.in);
				String in = sc.next();

				memory.set(memPos, (int)in.charAt(0));

			} else {
				codePos++;
			}

		}

		return "Complete.";
	}

}