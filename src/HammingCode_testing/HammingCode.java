package HammingCode_testing;

import java.util.Arrays;
import java.util.Random;

import static HammingCode_testing.Useful_functions.*;
/***********************************************************************************************************************
 * @project: Lab2_test
 * @package: HammingCode_testing
 * -----------------------------------------------------
 * @authors: Hamed Haghjo
 * @date: 2021-09-11
 * @time: 10:52
 **********************************************************************************************************************/
public class HammingCode
{
    private H_Message message;
    private int NUM_OF_ERRORS_GENERATE;
    private int NUM_OF_ERRORS_FOUND;
    private String[] ERROR_LOCATION_GENERATED;
    private final int[] redundant_bit_location;
    private final String[] bits_location_in_binary;
    private final int[] ERROR_FOUND;

    public HammingCode(H_Message message)
    {
        this.message = message;
        this.NUM_OF_ERRORS_GENERATE = 0;
        this.NUM_OF_ERRORS_FOUND = 0;

        this.ERROR_LOCATION_GENERATED = new String[message.get_size()];
        this.bits_location_in_binary = new String[message.get_size()];
        this.redundant_bit_location = new int[message.get_size()];
        this.ERROR_FOUND = new int[(int)log_2(message.get_size())];

        Arrays.fill(ERROR_FOUND,0);
        Arrays.fill(this.ERROR_LOCATION_GENERATED, "0");

        for (int i = 0; i < bits_location_in_binary.length; i++)
        {
            String toAdd = "";
            for (int k = Integer.toBinaryString(i).length()-1; k >= 0 ; k--)
                toAdd = Integer.toBinaryString(i).substring(k);

            StringBuilder filler = new StringBuilder();
            for (int j = (int)log_2(bits_location_in_binary.length) - toAdd.length(); j > 0; j--)
                filler.append("0");

            bits_location_in_binary[i] = filler + toAdd;
        }

        for (int i = 0; i < redundant_bit_location.length; i++)
            if (isPowerOfTwo(i))
                redundant_bit_location[i] = 1;

    }

    /**
     * Generates NUM_OF_ERRORS_GENERATE amount of errors within the message
     * (also stores the location of these errors within the ERROR_LOCATION_GENERATED array)
     * @param errorMessage the message that goes through noise
     * @param NUM_OF_ERRORS set to the amount of errors
     * @param parity_bits_affected set to true if parity bits should be a victim of noise
     * @return returns the message after it went through noise
     */
    public H_Message generate_errors(H_Message errorMessage, int NUM_OF_ERRORS, boolean parity_bits_affected)
    {
        int size = errorMessage.get_size();
        this.NUM_OF_ERRORS_GENERATE = NUM_OF_ERRORS;

        if (NUM_OF_ERRORS > size)
            throw new IllegalArgumentException("The data size is " + size + ", and you want to generate " + NUM_OF_ERRORS + " errors");

        if ((NUM_OF_ERRORS < 0 || NUM_OF_ERRORS > size-(log_2(size) + 1)) && !parity_bits_affected)
            throw new IllegalArgumentException("Incorrect input, try again (Hint: 0 <= NUM_OF_ERRORS < " + (size-(log_2(size) + 1)) + ")");

        Random random = new Random();
        this.NUM_OF_ERRORS_GENERATE = NUM_OF_ERRORS;
        int amount = NUM_OF_ERRORS;
        String[] indexAltered = new String[size];
        Arrays.fill(indexAltered, "0");

        if (parity_bits_affected)
        {
            while(amount > 0)
            {
                int new_random = random.nextInt(size);
                if (!indexAltered[new_random].equals("x"))
                {
                    indexAltered[new_random] = "x";
                    errorMessage.getData()[new_random] = (errorMessage.getData()[new_random] == 1) ? 0 : 1;
                    amount--;
                }
            }
        }
        else
        {
            while(amount > 0)
            {
                int new_random = random.nextInt(size);
                if (!isPowerOfTwo(new_random))
                {
                    if (!indexAltered[new_random].equals("x")) //KOM HIT
                    {
                        indexAltered[new_random] = "x";
                        errorMessage.getData()[new_random] = (errorMessage.getData()[new_random] == 1) ? 0 : 1;
                        amount--;
                    }
                }
            }
        }
        ERROR_LOCATION_GENERATED = indexAltered;
        return errorMessage;
    }

    public void send_message(HammingCode receiver, int NUM_OF_ERRORS, boolean parity_bits_affected)
    {
        //The two parity checks
        this.s_parity_check();
        this.s_single_parity_check();

        //Receiver receives the message from the sender after it went through "noise", generating NUM_OF_ERRORS bit flips
        //receiver.receive_message(receiver, this.generate_errors(this.message, NUM_OF_ERRORS, parity_bits_affected));
        this.generate_errors(this.message, NUM_OF_ERRORS, parity_bits_affected);
        receiver.receive_message(receiver, this.message);
    }

    private void receive_message(HammingCode receiver, H_Message message)
    {
        receiver.message = message;
        receiver.r_parity_check();
        receiver.r_single_parity_check();
    }

    /**
     * Sets the parity for the first parity bit
     * If sum of all bits except first bit is even, then it leaves the first bit as 0
     * If sum of all bits except first bit is odd, then it flips the first bit
     */
    private void s_single_parity_check()
    {
        int sum = 0;
        for (int i = 0; i < this.message.getData().length; i++)
            sum += this.message.getData()[i];

        if ((sum % 2 != 0 && this.message.getData()[0] == 0) || (sum % 2 != 0 && this.message.getData()[0] == 1))
            this.message.getData()[0] = (this.message.getData()[0] == 0) ? 1 : 0;
    }

    /**
     * Sender has the responsibility of flipping the redundant bits
     * CAUTION: This function is only for the sender, assuming no noise in message
     */
    private void s_parity_check()
    {
        int toDecrease = 0;
        for (int i = 1; i < this.message.get_size(); i++)
        {
            if (redundant_bit_location[i] == 1)
            {
                int sum = 0;
                toDecrease++;
                String temp = bits_location_in_binary[i];

                for (int j = i; j < this.message.get_size(); j++)
                {
                    if (bits_location_in_binary[j].charAt((int) log_2(this.message.get_size()) - toDecrease) == '1')
                        sum += this.message.getData()[j];
                }
                if (sum % 2 != 0)
                    this.message.getData()[i] = 1;
            }
        }
    }

    /** Checks parity for the first parity bit
     */
    private void r_single_parity_check()
    {
        int sum = 0;
        for (int i = 0; i < this.message.getData().length; i++)
            sum += this.message.getData()[i];

        if (sum % 2 != 0 && this.message.getData()[0] == 0)
            NUM_OF_ERRORS_FOUND++;
        else if (sum % 2 != 0 && this.message.getData()[0] == 1)
            NUM_OF_ERRORS_FOUND++;

    }

    private void r_parity_check()
    {
        int toDecrease = 0;
        for (int i = 1; i < this.message.get_size(); i++)
        {
            int indexHelper = (int) log_2(this.message.get_size());
            if (redundant_bit_location[i] == 1)
            {
                int sum = 0;
                toDecrease++;
                indexHelper -= toDecrease;
                String temp = bits_location_in_binary[i];
                for (int j = i; j < this.message.get_size(); j++)
                {
                    if (bits_location_in_binary[j].charAt((int) log_2(this.message.get_size()) - toDecrease) == '1')
                        sum += this.message.getData()[j];
                }
                if ((this.message.getData()[i] == 0 && sum % 2 != 0) || (this.message.getData()[i] == 1 && sum % 2 != 0))
                    ERROR_FOUND[indexHelper] = 1;
            }
        }
        NUM_OF_ERRORS_FOUND++;
    }

    /**
     * A simple getter-function to get the message from the sender/receiver
     * @return returns the message
     */
    public H_Message getMessage()
    {
        return message;
    }

    public String[] get_ERROR_LOCATION_GENERATED()
    {
        return ERROR_LOCATION_GENERATED;
    }

    /**
     * Prints the message that the sender/receiver has
     */
    public void printMessage()
    {
        System.out.println(Arrays.toString(message.getData()));
    }

    public void printMessageSpecial()
    {
        if (Math.sqrt((double)message.get_size()) % 1 == 0)
            for (int i = 1; i < message.getData().length + 1; i++)
            {
                System.out.print(message.getData()[i-1] + "  ");
                if (i % Math.sqrt((double)message.get_size()) == 0)
                    System.out.println("");
            }
        else
        {
            System.out.println(Arrays.toString(this.message.getData()));
        }
    }

    public void printErrorLocation()
    {
        for (int i = 0; i < ERROR_LOCATION_GENERATED.length; i++)
            if (ERROR_LOCATION_GENERATED[i].equals("x"))
                System.out.println("ERROR AT INDEX: " + i);
    }

    public static void main(String[] args)
    {
        int message_size = 1024;
        H_Message sending_message = new H_Message(message_size);
        sending_message.generateRandomMessage();

        H_Message temp_message_receiver = new H_Message(message_size);
        temp_message_receiver.generateRandomMessage();

        HammingCode sender = new HammingCode(sending_message);
        HammingCode receiver = new HammingCode(temp_message_receiver);

        System.out.println("-------------MESSAGE BEFORE BEING SENT-------------");
        sender.printMessageSpecial();
        sender.send_message(receiver, 1, false);
        System.out.println("-------------MESSAGE BEFORE BEING SENT-------------");
        System.out.println("\n");
        System.out.println("SENDING MESSAGE...");
        System.out.println("\n");
        System.out.println("--------MESSAGE AFTER SENDER'S PARITY CHECKS--------");
        sender.printMessageSpecial();

        System.out.println("****************************************************");

        for (int i = 0; i < sender.ERROR_LOCATION_GENERATED.length; i++)
            if (sender.ERROR_LOCATION_GENERATED[i].equals("x"))
                System.out.println("ERROR WAS GENERATED AT INDEX:   " + i + ", IN BINARY:   " + sender.bits_location_in_binary[i]);

        StringBuilder index_answer = new StringBuilder();
        for (int j : receiver.ERROR_FOUND) index_answer.append(j);
        if (Integer.parseInt(index_answer.toString(),2) > 0)
            System.out.println("ERROR WAS DETECTED AT INDEX:    " + Integer.parseInt(index_answer.toString(), 2) + ", IN BINARY:   " + index_answer);
        else
            System.out.println("No errors detected from the redundant bits (exl. first parity bit)");

        System.out.println("****************************************************");
        System.out.println("--------MESSAGE AFTER SENDER'S PARITY CHECKS--------");
        System.out.println("\n");
        /* Part of sending algorithm
        sender.s_parity_check();
        sender.s_single_parity_check();
        System.out.println("------------Senders message:------------");
        sender.printMessageSpecial();
        sender.generate_errors(sender.message, 1, false);
        System.out.println("-------------------------");
        //sender.r_parity_check();
        //sender.r_single_parity_check();
        sender.printMessageSpecial();
        /** Ugly message fix
        for (int i = 0; i < error_indexes.length; i++)
            sender.message.getData()[error_indexes[i]] = (sender.message.getData()[error_indexes[i]] == 0) ? 1 : 0;

        sender.r_parity_check();
        sender.r_single_parity_check();
        System.out.println("At least: " + sender.NUM_OF_ERRORS_FOUND + " errors");
        //System.out.println("-----------FIXED-------------");
        //sender.printMessageSpecial();
        //System.out.println("-----------FIXED-------------");
        //To do:
        //Fix NUM_OF_ERRORS_FOUND = 2 om NUM*/
    }

}
