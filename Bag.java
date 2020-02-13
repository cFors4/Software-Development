import java.util.ArrayList;
import java.util.Random;

class Bag {

    //Initialises attributes for the bag

    //The bag of inverted colour which this bag is paired with
    public Bag pair;
    //The contents of the bag
    public ArrayList<Integer> pebbles;

    //Adds multiple pebbles to bag
    //@Param ArrayList<Integer> input - The pebbles to be added
    //@Return void
    public  void in(ArrayList<Integer> input){
            pebbles.addAll(input);
    }

    //Adds one pebble to bag
    //@Param int input - The pebble to be added
    //@Return void
    public  void in(int input){pebbles.add(input); }

    //Removes a random pebble from the bag. Also empties white bag into black bag
    //if black bag is empty
    //@Return Integer - The pebble which has been removed
    public Integer out(){

        Random rand = new Random();

        //If black bag empty...
        if((pebbles.size() == 0)){
            //And corresponding white bag is not empty...
            if(pair.pebbles.size() != 0) {

                //Empty contents of white bag into corresponding black bag
                ArrayList<Integer> copy2 = new ArrayList<Integer>(pair.pebbles);
                pebbles = copy2;
                int randInt = rand.nextInt(pebbles.size());
                pair.pebbles.clear();

                //Return pebble
                Integer pebble = pebbles.get(randInt);
                pebbles.remove(randInt);
                return pebble;
            }
            else{return -1;}

        //If bag is not empty, remove and return pebble
        }else {
            int randInt = rand.nextInt(pebbles.size());
            Integer pebble = pebbles.get(randInt);
            pebbles.remove(randInt);
            return pebble;
        }

    }

    //Initialise the bag to have an empty array of pebbles in it
    public void initPebbles(){
        pebbles = new ArrayList<Integer>();
    }
}