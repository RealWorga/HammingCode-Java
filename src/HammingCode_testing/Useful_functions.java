package HammingCode_testing;

/***********************************************************************************************************************
 * @project: Lab2_test
 * @package: HammingCode_testing
 * -----------------------------------------------------
 * @authors: Hamed HaghjoHaghjo
 * @date: 2021-09-11
 * @time: 14:58
 **********************************************************************************************************************/
public class Useful_functions
{
    public static double log_2(int N) //Might come in handy
    {
        return (double)(Math.log(N) / Math.log(2));
    }

    public static boolean isPowerOfTwo(int n)
    {
        final double v = Math.log(n) / Math.log(2);
        return (int)(Math.ceil(v)) == (int)(Math.floor(v));
    }

    public static void main(String[] args)
    {
        System.out.println(isPowerOfTwo( 9));

    }
}
