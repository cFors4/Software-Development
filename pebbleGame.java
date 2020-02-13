

import java.util.Scanner;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Random;
import java.io.FileWriter;
import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

public class pebbleGame {

    //Initialise bags
    volatile static ArrayList<Bag> blackBags = new ArrayList<>();
    volatile static ArrayList<Bag> whiteBags = new ArrayList<>();

    //Initialise variables used for statistics
    volatile static int turns = 0;
    public static Long timeStart;
    public static int numPlayers;


    public static void main(String[] args) {

        //Get initial timestamp
        timeStart = System.currentTimeMillis();

        //Create all bags and initialise them with pebbles
        Bag bagA = new Bag();
        bagA.initPebbles();
        Bag bagB = new Bag();
        bagB.initPebbles();
        Bag bagC = new Bag();
        bagC.initPebbles();

        Bag bagX = new Bag();
        bagX.initPebbles();
        Bag bagY = new Bag();
        bagY.initPebbles();
        Bag bagZ = new Bag();
        bagZ.initPebbles();

        //Add bags to their own array, to be indexed
        Bag[] BlackBags = {bagA, bagB, bagC};
        Bag[] WhiteBags = {bagX, bagY, bagZ};

        //Pair bags and add bags to volatile, dynamic array
        for (int i = 0; i < 3; i++) {
            BlackBags[i].pair = WhiteBags[i];
            WhiteBags[i].pair = BlackBags[i];
            blackBags.add(BlackBags[i]);
            whiteBags.add(WhiteBags[i]);
        }

        //User input. Goes back to beginning if any error is caught
        while (true) {
            label:try {
                numPlayers = inputNumPlayers();
                if(numPlayers == -1){
                    System.out.println("You inputted a negative number. Try again.");
                    break label;
                }

                for (int i = 0; i < 3; i++) {
                    ArrayList<Integer> pebbles = loadPebbles(i, numPlayers);
                    if (pebbles == null){
                        break label;
                    }
                }
                break;
            } catch (Exception e) {
                System.out.println("There was an error with your input. Try again." + e);
                continue;
            }
        }

        //Starts variable amount of threads, based on amount of players
        for (int x=0; x<numPlayers; x++){
            Player a = new pebbleGame().new Player();
            a.start();
            System.out.println("Started Thread:" + x);
        }
    }

    //Player thread
    class Player extends Thread {

        //Initialise player's hand and hand sum attributes
        public ArrayList<Integer> hand;
        public int handSum;

        //When Player is started...
        public void run() {

            //Create unique player log file
            File file = createFile();
            if(file == null){
                System.out.println("No output file made");
            }

            System.out.println("Player running");

            //Player chooses random black bag and takes 10
            //random pebbles from that bag, one at a time
            hand = new ArrayList<Integer> ();
            int randInt = new Random().nextInt(blackBags.size());
            hand = take10(hand,randInt);

            //Keep taking turns until game ends
            while(notFinished()){
                takeTurn(file);

                //Used for additional feature (statistics)
                turns += 1;
            }

            //Additional feature output (statistics)
            String winner = Long.toString(Thread.currentThread().getId() - 10);
            Long timeEnd = System.currentTimeMillis();
            String time = Long.toString((timeEnd - timeStart) / 1000);
            String strTurns = Integer.toString(turns);
            boolean successfulEnd = printEndScore(winner,time,strTurns);

            //End the game
            System.exit(0);
        }

        //Additional feature: Prints end score and writes to file
        //@Param String winner - Name of player who won the game
        //@Param String time - Time which the game took to run
        //@Param String strTurns - Amount of turns which the game took to end
        //@Return boolean - Whether the function wrote to the file correctly or not
        public boolean printEndScore(String winner,String time,String strTurns){
            try {
                System.out.println("--------------The game has ended!--------------");
                System.out.println("Player " + winner + " won the game! with a total of "+numPlayers+" players in that game");
                System.out.println("The game took " + time + " seconds");
                System.out.println("There were " + strTurns + " turns for the game to finish");
                System.out.println("-----------------------------------------------\n\n");

                File file = new File("logOutput.txt");

                try {
                    file.createNewFile();
                }catch(Exception e){return false;}
                try {
                    FileWriter writer = new FileWriter(file,true);
                    writer.write("--------------The game has ended!--------------\n");
                    writer.write("Player " + winner + " won the game! with a total of "+numPlayers+" players in that game\n");
                    writer.write("The game took " + time + " seconds\n");
                    writer.write("There were " + strTurns + " turns for the game to finish\n");
                    writer.write("-----------------------------------------------\n\n");
                    writer.close();
                    return true;

                }catch (Exception e){return false;}
            }catch (Exception e){return false;}

        }

        //Creates the player's output file
        //@Return File - The created file
        public File createFile(){
            File file = new File("Player"+Long.toString(Thread.currentThread().getId()-10)+"output.txt");

            //Make sure that the file is empty when creating file
            if (file.exists() && file.isFile())
            {
                file.delete();
            }
            try {
                file.createNewFile();
                return file;
            }catch(Exception e){return null;}
        }

        //Takes 10 pebbles from a selected bag, one at a time
        //@Param ArrayList<Integer> tempHand - The hand of the player (private)
        //@Param int randInt - The index of the selected bag
        //@Return ArrayList<Integer> - The updated hand
        public ArrayList<Integer> take10(ArrayList<Integer> tempHand,int randInt){

            //Add each pebble, one at a time
            for (int i = 0; i < 10; i++) {
                tempHand.add(blackBags.get(randInt).out());
                System.out.println(tempHand);
            }

            //Makes sure hand is of size 10
            if(tempHand.size() != 10){
                System.out.println("cant take initial 10");
                return null;
            }else{return tempHand;}
        }

        //Checks to make sure the game hasn't finished (hand sum is / is not equal to 100)
        //@Return boolean - Whether or not the game has finished
        public boolean notFinished() {
            handSum = 0;
            for (int i = 0; i < hand.size(); i++) {
                handSum += hand.get(i);
            }
            if (handSum == 100 && hand.size() == 10) {
                return false;
            } else {
                return true;
            }
        }

        //Takes a player's turn. Draws a pebble from a black bag and discards a random
        //pebble from hand to the paired white bag
        //@Param File - The player's output file
        //@Return - void
        public void takeTurn(File file) {
            System.out.println("Current Player " + Long.toString(Thread.currentThread().getId()-10)+" has a sum of "+handSum);

            //Chooses random black bag (and corresponding white bag)
            int randInt = new Random().nextInt(blackBags.size());

            //If both white and black bag are empty, choose another. Else,
            if(blackBags.get(randInt).pebbles.size() == 0 && whiteBags.get(randInt).pebbles.size() == 0) {
                randInt -= 1;
            }else {
                //Draw then discard
                hand = draw(hand,file,randInt);
                hand = discard(hand,file,randInt);
            }
        }

        //Draw a random pebble from a given black bag and writes to file
        //@Param ArrayList<Integer> tempHand - The players hand (private)
        //@Param File file - The player's output file
        //@Param int randInt - The index of both the black and white bags
        //@Return ArrayList<Integer> - The player's hand
        public synchronized ArrayList<Integer> draw(ArrayList<Integer> tempHand, File file, int randInt){

            //
            int drawn = blackBags.get(randInt).out();
            tempHand.add(drawn);

            try {
                FileWriter writer = new FileWriter(file,true);
                writer.write("Player " + Thread.currentThread().getId() + " has drawn " + drawn + " from bag " + randInt+"\n");
                writer.write("Player " + Thread.currentThread().getId() + " hand is " + hand+"\n");
                writer.close();

            }catch (Exception e){System.out.println("could not append");}
            return tempHand;
        }

        //Discards a random pebble from the player's hand to a given bag and writes to output file
        //@Param ArrayList<Integer> tempHand - The players hand (private)
        //@Param File file - The player's output file
        //@Param int randInt - The index of both the black and white bags
        //@Return ArrayList<Integer> - The player's hand
        public synchronized ArrayList<Integer> discard(ArrayList<Integer> tempHand, File file, int randInt){
            int discarded = new Random().nextInt(tempHand.size());
            int discardedElement = tempHand.get(discarded);
            whiteBags.get(randInt).in(discardedElement);
            tempHand.remove(discarded);

            try {
                FileWriter writer = new FileWriter(file,true);

                writer.write("Player " + Thread.currentThread().getId() + " has discarded " + discardedElement + " to bag " + randInt+"\n");
                writer.write("Player " + Thread.currentThread().getId() + " hand is " + hand+"\n");
                writer.close();

            }catch (Exception e){System.out.println("could not append");}
            return tempHand;
        }
    }

    //Checks to see whether the user has inputted "e" or not
    //@Param String s - The user's inputted string
    //@Return boolean - Whether or not the user inputted "e"
    public static boolean checkExit (String s){
        if (s.equals("e") || s.equals("E")) {
            System.out.println("Exiting...");
            return true;
        }else{return false;}
    }

    //Asks the user for a number of players
    //@Return int - The number of players which the user inputted. -1 if invalid
    public static int inputNumPlayers(){
        Scanner kb = new Scanner(System.in);
        System.out.println("Input number of players: ");
        String numPlayers_str = kb.nextLine();
        int numPlayers = -1;

        if(checkExit(numPlayers_str)){
            System.exit(0);
        }
        try {
            numPlayers = Integer.parseInt(numPlayers_str);
            if (numPlayers <= 0) {
                System.out.println("no negative numbers");
                numPlayers = -1;
            }
        }catch(Exception e){numPlayers = -1;}
        return numPlayers;
    }

    //Loads, for one bag, the contents of the given CSV file into the bag's pebbles.
    //@Param int i - The index of the given bag
    //@Param int numPlayers - Used to make sure the CSV file is big enough to add to bag
    //@Return ArrayList<Integer> - The bag's contents to be added
    public static ArrayList<Integer> loadPebbles(int i, int numPlayers){
        Scanner kb = new Scanner(System.in);
        System.out.println("Input location of bag number " + Integer.toString(i) + " to load: ");
        String fileString = kb.nextLine();

        if(checkExit(fileString)){
            System.exit(0);
        }

        ArrayList<Integer> pebbles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileString))) {

            String line = br.readLine();
            for (String s : line.split(",")) {
                //check for negative integers
                int x = Integer.parseInt(s);
                if(x>=0) {
                    pebbles.add(Integer.parseInt(s));
                }else{
                    System.out.println("no negative numbers");
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("file not found");
            return null;
        }
        if (pebbles.size() > numPlayers * 11) {
            blackBags.get(i).in(pebbles);
            System.out.println("Pebbles in Bag");
        } else {
            System.out.println("csv too small for number of players");
            return null;
        }
        return pebbles;
    }

}




