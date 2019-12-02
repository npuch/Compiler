import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Hashtable;

public class Main {
    static int pc = 0;
    static int offset = 0; //for position on stack
    static int varLoc = 0; //for location of var on stack
    static Hashtable<String, Integer> intTable = new Hashtable<String, Integer>(); //holds variables contents
    static Hashtable<String, Integer> varOffsetTable = new Hashtable<String, Integer>(); //holds var's offset
    static Hashtable<Integer, String> offsetVarTable = new Hashtable<Integer, String>();
    static ArrayList<Integer> stack = new ArrayList<Integer>(); 
    public static void reader(String fileName){
        BufferedReader br = null;
        ArrayList<byte[]> list = new ArrayList<byte[]>();
        try {
            File file = new File(fileName);
            br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                //System.out.println(st);
                String[] tokens = st.split(" ");
                String statement = tokens[0];
                if(statement.equals("subr"))
                    subr(list);
                else if(statement.equals("pushi")){
                    Integer intObj = new Integer(tokens[1]);
                    pushint(list, intObj);
                }
                else if(statement.equals("pushv")){
                    pushv(list, tokens[1]);
                }
                else if(statement.equals("printi"))
                    printi(list, tokens[1]);
                else if(statement.equals("printv"))
                    printv(list, tokens[1]);
                else if(statement.equals("ret"))
                    ret(list);
                else if(statement.equals("decl"))
                    decl(list, tokens[1]);
                else if(statement.equals("popv"))
                    popv(list, tokens[1]);
                else if(statement.equals("add"))
                    add(list);
                else if(statement.equals("sub"))
                    sub(list);
                else if(statement.equals("mul"))
                    mul(list);
                else if(statement.equals("div")){
                    div(list);
                }
                else if(statement.equals("swp")){
                    swp(list);
                }
            }
            printList(list);
        } catch (final IOException e) {
            System.out.println("Could not read from file");
        }
        finally{
            try{br.close();}
            catch(IOException e){ System.out.println("Could not close file");}
            finally{System.out.println("Attempt to read file done");}
        }
    }
    public static void subr(ArrayList<byte[]> list){
        pushi(list, "16");
        pushi(list, "17");
        pushi(list, "1");
        byte[] opCode = {44};
        addOpCode(list, opCode);
        byte[]opCode2 = {0};
        addOpCode(list, opCode2);
    }
    
    public static void printi(ArrayList<byte[]> list, String value){
        pushi(list, value);
        //146 printi
        byte b = (byte)146;
        byte[] opCode = {b};
        addOpCode(list, opCode);
    }

    //for pusing integers and adding to table
    public static void pushint(ArrayList<byte[]> list, Integer value) {
        //70 pushi
        byte[] opCode = {70};
        addOpCode(list, opCode);

        //add int 
        addInt(list, value.toString());

        stack.add(value);
        varLoc++;
   }

    //for pushing integers on stack and not adding to table
    public static void pushi(ArrayList<byte[]> list, String value){
         //70 pushi
         byte[] opCode = {70};
         addOpCode(list, opCode);

        //add int 
        addInt(list, value);
    }
    public static void pushv(ArrayList<byte[]> list, String name){
        //push var index to be pushed
        Integer index = varOffsetTable.get(name);
        pushi(list, Integer.toString(index));

        //update stack from intTable
        Integer val = intTable.get(name);
        stack.add(val);
        varLoc++;
        
        byte[] opCode = {74}; // pushvi 74
        addOpCode(list, opCode);
    }

    public static void popv(ArrayList<byte[]> list, String name){ 
        //offset on stack
        Integer offsetVar = varOffsetTable.get(name);
        pushi(list, Integer.toString(offsetVar));
        
        //set var equal to value in intTable
        Integer value = stack.get(offsetVar);
        intTable.put(name, value);
        varLoc--;

        byte[] opCode = {80}; //popv 80
        addOpCode(list, opCode);
    }

    public static void printv(ArrayList<byte[]> list, String name){
        //push var to index to be printed
        Integer index = varOffsetTable.get(name);
        pushi(list, Integer.toString(index));

        //push pushvi opcode 74
        byte[] opCode = {74};
        addOpCode(list, opCode);

        //push print opcode 146
        byte b = (byte)146;
        byte[] opCode2 = {b};
        addOpCode(list, opCode2);
    }

    public static void add(ArrayList<byte[]> list){
        byte[] opCode = {100}; 
        addOpCode(list, opCode);
    }
    public static void sub(ArrayList<byte[]> list){
        byte[] opCode = {104}; 
        addOpCode(list, opCode);
    }
    public static void mul(ArrayList<byte[]> list){
        byte[] opCode = {108}; 
        addOpCode(list, opCode);
    }
    public static void div(ArrayList<byte[]> list){
        byte[] opCode = {112}; 
        addOpCode(list, opCode);
    }
    public static void swp(ArrayList<byte[]> list){
        byte[] opCode = {94}; 
        addOpCode(list, opCode);
    }

    public static void ret(ArrayList<byte[]> list){
        pushi(list, "0");
        byte[] opCode = {77};  //77 popa 
        addOpCode(list, opCode);
        byte[] opCode2 = {48}; //48 ret
        addOpCode(list, opCode2);
    }

    public static void decl(ArrayList<byte[]> list, String name){
        pushi(list, "0");
        varOffsetTable.put(name, offset);
        offsetVarTable.put(offset, name);
        offset++;
    }

    public static void addOpCode(ArrayList<byte[]> list, byte[] opCode){
        String preCode = String.format("%02x", opCode[0]);
        System.out.print(preCode);
        list.add(opCode);
        pc++;
    }
    public static void addInt(ArrayList<byte[]> list, String value){
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(Integer.parseInt(value));

        byte[] bytesToAdd = byteBuffer.array();
        for(byte b: bytesToAdd){
            String st = String.format("%02X", b);
            System.out.print(st);
        }
        list.add(bytesToAdd);
        pc+=4;
    }

    public static void printList(ArrayList<byte[]> list){
        FileOutputStream fos;
        try{
            fos = new FileOutputStream("C:\\Users\\Nathan\\Desktop\\simple_math_output.bin");

            for(byte[] b : list){
                try{
                    fos.write(b);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            try{
                fos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        catch(FileNotFoundException e2){
            e2.printStackTrace();
        }
    }

    public static void main( final String[] args) {
        System.out.println("Project Compiler\n");
        String fileName = args[0];
        reader(fileName);
    }
}