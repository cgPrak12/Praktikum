package terrain;

import java.io.File;
import java.util.Random;

public class TestCase {

        public static void main(String[] args) {
                
                Random r = new Random();
                
                Block TestBlock1 = new Block(1, 2);
                
                for(int i = 0; i < 256; i++)
                {
                        for(int j = 0; j < 256; j++)
                        {
                                for(int k = 0; k < 5; k++)
                                {
                                        TestBlock1.setInfo(i, j, k, 1.0f);//r.nextFloat()*100);
//                                        System.out.println("i: " + i + ", j: " + j + ", value: " + TestBlock1.getInfo(i, j, k));
                                }
                        }        
                }
                System.out.println();
                
                System.out.println("writing to file");
                File file = BlockUtil.writeBlockData(TestBlock1);
                System.out.println("done");
                System.out.println("reading from file");
                Block TestBlock2 = BlockUtil.readBlockData(file);
                System.out.println("done");        
                
                for(int i = 0; i < 256; i++)
                {
                        for(int j = 0; j < 256; j++)
                        {
                                for(int k = 0; k < 5; k++)
                                {
//                                	System.out.println("bla");
                                        if(TestBlock1.getInfo(i, j, k) == TestBlock2.getInfo(i, j, k))
                                        {        
//                                                System.out.println("[" + i + "]" + "[" + j + "]" + "[" + k + "]"+" TRUE");
                                                //System.out.println(TestBlock1.getInfo(i, j, k) + " - " + TestBlock2.getInfo(i, j, k));
                                        }
                                        else
                                        {
//                                                System.out.println("[" + i + "]" + "[" + j + "]" + "[" + k + "]"+" FALSE");
//                                                System.out.println(TestBlock1.getInfo(i, j, k) + " - " + TestBlock2.getInfo(i, j, k));
                                                break;
                                        }
                                }
                        }
                }
                System.out.println();
        }

}
