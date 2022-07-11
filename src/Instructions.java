
public class Instructions {
    public static int toDic(String arg1){
        return Integer.parseInt(arg1,2)%256;
    }
    public static String toBin(int arg1){
        arg1%=256;
        String arg = Integer.toBinaryString(arg1);
        while (arg.length()<Registers.Size){
            arg = "0"+arg;
        }
        return arg;
    }
    public static boolean checkOverflow(String r1,String r2,String res,boolean add){
        boolean S1 = r1.charAt(0)=='0';
        boolean S2 = r2.charAt(0)=='0';
        boolean S3 = res.charAt(0)=='0';
        if(!add){
            S2 = !S2;
        }
        if(S1==S2 && S1!=S3){
            return true;
        }
        return false;
    }


    public static boolean checkC(int arg1,int arg2){
        if(arg1>0 && arg2>0){
            int res = arg1+arg2;
            if(res>=256){
                return true;
            }
        }
        return false;
    }
    public static void ADD(int r1,int r2){
        int arg1 = toDic(Registers.GPRS[r1]);
        int arg2 = toDic(Registers.GPRS[r2]);
        int value = arg1+arg2;
        String res = toBin(value);
        res = res.substring(res.length()-8);
        boolean c= checkC(arg1,arg2);
        Registers.SR.setC(c?'1':'0');
        boolean V= checkOverflow(Registers.GPRS[r1],Registers.GPRS[r2],res,true);
        Registers.SR.setV(V?'1':'0');
        boolean N = (res.charAt(0)=='1');
        Registers.SR.setN(N?'1':'0');
        Registers.SR.setZ((toDic(res)==0)?'1':'0');
        Registers.SR.setS((N^V)?'1':'0');
        Registers.GPRS[r1] = res;
        Registers.SR.print();
        System.out.println("new value for R"+r1+" is "+res);
    }


    public static void SUB(int r1, int r2) {
        int arg1 = toDic(Registers.GPRS[r1]);
        int arg2 = toDic(Registers.GPRS[r2]);
        int value = arg1-arg2;
        String res = toBin(value);
        res = res.substring(res.length()-8);
        boolean V= checkOverflow(Registers.GPRS[r1],Registers.GPRS[r2],res,false);
        Registers.SR.setV(V?'1':'0');
        boolean N = (res.charAt(0)=='1');
        Registers.SR.setN(N?'1':'0');
        Registers.SR.setZ((toDic(res)==0)?'1':'0');
        Registers.SR.setS((N^V)?'1':'0');
        Registers.GPRS[r1] = res;
        Registers.SR.print();
        System.out.println("new value for R"+r1+" is "+res);
    }

    public static void MUL(int r1, int r2) {
        int arg1 = toDic(Registers.GPRS[r1]);
        int arg2 = toDic(Registers.GPRS[r2]);
        int value = arg1*arg2;
        String res = toBin(value);
        res = res.substring(res.length()-8);
        Registers.GPRS[r1] = res;
        boolean N = (res.charAt(0)=='1');
        Registers.SR.setN(N?'1':'0');
        Registers.SR.setZ((toDic(res)==0)?'1':'0');
        Registers.SR.print();
        System.out.println("new value for R"+r1+" is "+res);
    }

    public static void LDI(int r1,String IMM) {
        IMM = IMM.charAt(0)+""+IMM.charAt(0)+IMM;
        Registers.GPRS[r1] = IMM;
        System.out.println("new value for R"+r1+" is "+IMM);
    }
    public static void BEQZ(int r1,int  IMM) {
        if (toDic(Registers.GPRS[r1])==0) {
            Registers.PC =(short)(Registers.PC + IMM -1);
            DataPath.instruction = null;
            DataPath.opcode = null;
            DataPath.Stages[1] = -1;
            DataPath.Stages[2] = -1;
            DataPath.flag = false;
            System.out.println("new value for PC is "+Registers.PC);
            if(Registers.PC>=InstructionMemory.add){
                DataPath.end = true;
            }
        }
    }

    public static void AND(int r1, int r2) {
        int arg1 = toDic(Registers.GPRS[r1]);
        int arg2 = toDic(Registers.GPRS[r2]);
        int value = arg1&arg2;
        String res = toBin(value);
        res = res.substring(res.length()-8);
        boolean N = (res.charAt(0)=='1');
        Registers.SR.setN(N?'1':'0');
        Registers.SR.setZ((toDic(res)==0)?'1':'0');
        Registers.GPRS[r1] = res;
        Registers.SR.print();
        System.out.println("new value for R"+r1+" is "+res);
    }
    public static void OR(int r1, int r2) {
        int arg1 = toDic(Registers.GPRS[r1]);
        int arg2 = toDic(Registers.GPRS[r2]);
        int value = arg1|arg2;
        String res = toBin(value);
        res = res.substring(res.length()-8);
        boolean N = (res.charAt(0)=='1');
        Registers.SR.setN(N?'1':'0');
        Registers.SR.setZ((toDic(res)==0)?'1':'0');
        Registers.GPRS[r1] = res;
        System.out.println(res);
        Registers.SR.print();
        System.out.println("new value for R"+r1+" is "+res);
    }

    public static void JR(int r1 , int r2){
        Registers.PC = Short.parseShort(Registers.GPRS[r1] + Registers.GPRS[r2],2);
        DataPath.instruction = null;
        DataPath.opcode = null;
        DataPath.flag = true;
        DataPath.Stages[1] = -1;
        DataPath.Stages[2] = -1;
        DataPath.flag = false;
        System.out.println("new value for PC is "+Registers.PC);
        if(Registers.PC>=InstructionMemory.add){
            DataPath.end = true;
        }
    }

    public static void SLC(int r1 ,int IMM){
        String arg1 = toBin(toDic(Registers.GPRS[r1])<<IMM | toDic(Registers.GPRS[r1])>>>(8 - IMM));
        arg1 = arg1.substring(arg1.length()-8);
        boolean N = (arg1.charAt(0)=='1');
        Registers.SR.setN(N?'1':'0');
        Registers.SR.setZ((toDic(arg1)==0)?'1':'0');
        Registers.GPRS[r1] = arg1;
        Registers.SR.print();
        System.out.println("new value for R"+r1+" is "+arg1);
    }

    public static void SRC( int r1 , int IMM){
        String arg1 = toBin(toDic(Registers.GPRS[r1])>>>(IMM) | toDic(Registers.GPRS[r1])<<(8 - IMM));
        arg1 = arg1.substring(arg1.length()-8);
        boolean N = (arg1.charAt(0)=='1');
        Registers.SR.setN(N?'1':'0');
        Registers.SR.setZ((toDic(arg1)==0)?'1':'0');
        Registers.GPRS[r1] = arg1;
        Registers.SR.print();
        System.out.println("new value for R"+r1+" is "+arg1);
    }

    public static void LB(int r1 , int address){
        Registers.GPRS[r1]=DataMemory.getData(address);
        System.out.println("new value for R"+r1+" is "+DataMemory.getData(address));
    }

    public static void SB(int r1 , int address){
        DataMemory.setData(address, Registers.GPRS[r1]);
        System.out.println("new value for m["+address+"] is "+Registers.GPRS[r1]);
    }

    public static void main(String[] args) {
        /*Registers.GPRS[0] = "00011001";
        System.out.println(ADD(0,1));
        System.out.println(SUB("101","10"));
        System.out.println(MUL("10","101"));
        LDI("10","101");
        BEQZ("0000","101");
        System.out.println(AND("10","11"));
        System.out.println(OR("10","11"));
        JR("100","111");
        Registers.GPRS[0] = "11";
       // SLC(0,2);
        Registers.GPRS[1] = "11";
        //SRC(0,2);
        System.out.println("Adding");
        ADD(0,1);
        System.out.println("Subtracting");
        SUB(0,1);
        System.out.println("Multipling");
        MUL(0,1);
        System.out.println("Anding");
        AND(0,1);
        System.out.println("ORing");
        OR(0,1);*/
    }

}
