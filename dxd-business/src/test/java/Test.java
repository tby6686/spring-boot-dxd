public class Test {

    public static void main(String[] args) {


        //0.0214~0.022
        //0.0214
        for(double i=0.0214;i<0.022;i=i+0.000001){
            double x=i;
            double a = 35* Math.pow(1+x,10);
            double b = 35* Math.pow(1+x,9);
            double c = 35* Math.pow(1+x,8);
            System.out.println(a+b+c);
            if((a+b+c)>127.429 && (a+b+c)<127.431){
                System.out.println(i);
                break;
            }
        }

    }
}
