package HammingCode_testing;

import java.util.Arrays;

import static HammingCode_testing.Useful_functions.isPowerOfTwo;
import static HammingCode_testing.Useful_functions.log_2;


/***********************************************************************************************************************
 * @project: Lab2_test
 * @package: HammingCode_testing
 * -----------------------------------------------------
 * @authors: Hamed Haghjo
 * @date: 2021-09-11
 * @time: 10:42
 **********************************************************************************************************************/
public class H_Message
{
    private int[] data;
    private int MESSAGE_SIZE; //Inclusive of redundant parity bits and the first parity bit

    /**
     * Constructor for the message to be sent
     * @param MESSAGE_SIZE size should be a number that is a power of 2. (Excluding 1 because that is just plain stupid), Ex. MESSAGE_SIZE ∈ {2, 4, 8, 16, 32, 64, 128, 256, ...}
     */
    public H_Message(int MESSAGE_SIZE)
    {
        if (!((MESSAGE_SIZE % log_2(MESSAGE_SIZE))%2==0))
            throw new IllegalArgumentException("Please input a size that is a power of 2");

        this.MESSAGE_SIZE = MESSAGE_SIZE;
        data = new int[MESSAGE_SIZE];
    }

    /**
     * Generates a random message, with the redundant parity bits and first parity bit set to 0.
     */
    public void generateRandomMessage()
    {
        //One bit message?
        if (data.length <= 2)
            data[1] = (Math.random() < 0.5) ? 1 : 0;
        //Sets the redundant bits to 0 and generates random numbers for every other bit in the message
        else
        {
            for (int i = 0; i < data.length; i++)
                if (isPowerOfTwo(i))
                    data[i] = 0;
                else
                    data[i] = (Math.random() < 0.5) ? 1 : 0;
        }
    }

    public int[] getData()
    {
        return data;
    }

    public int get_size()
    {
        return MESSAGE_SIZE;
    }

    public void print_message()
    {
        System.out.println(Arrays.toString(data));
    }
}
