import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
public class Main {
    static int pc = 0;
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
                else if(statement.equals("pushi"))
                    pushi(list, tokens[1]);
                else if(statement.equals("printi"))
                    printi(list, tokens[1]);
                else if(statement.equals("ret"))
                    ret(list);
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
        pc++;
        byte[]opCode2 = {0};
        addOpCode(list, opCode2);
        pc++;
    }

    public static void printi(ArrayList<byte[]> list, String value){
        pushi(list, value);
        //146 printi
        byte b = (byte)146;
        byte[] opCode = {b};
        addOpCode(list, opCode);
    }

    public static void pushi(ArrayList<byte[]> list, String value){
         //70 pushi
         byte[] opCode = {70};
         addOpCode(list, opCode);

        //add int 
        addInt(list, value);
        pc += 5;
    }

    public static void ret(ArrayList<byte[]> list){
        pushi(list, "0");
        byte[] opCode = {77};  //77 popa 
        addOpCode(list, opCode);
        pc++;
        byte[] opCode2 = {48}; //48 ret
        addOpCode(list, opCode2);
        pc++;
    }

    public static void addOpCode(ArrayList<byte[]> list, byte[] opCode){
        String preCode = String.format("%02x", opCode[0]);
        System.out.print(preCode);
        list.add(opCode);
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
    }

    public static void printList(ArrayList<byte[]> list){
        FileOutputStream fos;
        try{
            fos = new FileOutputStream("C:\\Users\\Nathan\\Desktop\\basics_output.bin");

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