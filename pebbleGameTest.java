
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class pebbleGameTest {
    public pebbleGameTest(){}

    @Test
    public void testInputNumPlayers() throws Exception{
        InputStream sysInBackup = System.in;

        System.out.println("Testing input for number of players");
        ByteArrayInputStream in = new ByteArrayInputStream(("-5").getBytes());
        System.setIn(in);
        Assert.assertEquals(-1, pebbleGame.inputNumPlayers());
        System.out.println("Test passed with input -5!");
        System.setIn(sysInBackup);

        in = new ByteArrayInputStream(("incorrect").getBytes());
        System.setIn(in);
        Assert.assertEquals(-1, pebbleGame.inputNumPlayers());
        System.out.println("Test passed with input 'incorrect'!");
        System.setIn(sysInBackup);

        in = new ByteArrayInputStream(("5").getBytes());
        System.setIn(in);
        Assert.assertEquals(5, pebbleGame.inputNumPlayers());
        System.out.println("Test passed with input '5'!");
        System.setIn(sysInBackup);

        in = new ByteArrayInputStream(("0").getBytes());
        System.setIn(in);
        Assert.assertEquals(-1, pebbleGame.inputNumPlayers());
        System.out.println("Test passed with input 0!");
        System.setIn(sysInBackup);
    }
    @Test
    public void testLoadPebbles() throws Exception{
        InputStream sysInBackup = System.in;

        System.out.println("Testing loading pebbles from csv");
        ByteArrayInputStream in = new ByteArrayInputStream(("invalid.csv").getBytes());
        System.setIn(in);
        Assert.assertEquals(null, pebbleGame.loadPebbles(1,2));
        System.out.println("Test passed with input invalid.csv!");
        System.setIn(sysInBackup);

        in = new ByteArrayInputStream(("negativeTest.txt").getBytes());
        System.setIn(in);
        Assert.assertEquals(null, pebbleGame.loadPebbles(1,2));
        System.out.println("Test passed with input negativeTest.txt!");
        System.setIn(sysInBackup);

        in = new ByteArrayInputStream(("tooSmallTest.txt").getBytes());
        System.setIn(in);
        Assert.assertEquals(null, pebbleGame.loadPebbles(1,10));
        System.out.println("Test passed with input tooSmallTest.txt!");
        System.setIn(sysInBackup);

    }
    @Test
    public void testCheckExit() throws Exception{
        System.out.println("Testing check exit if user inputs e or not");
        Assert.assertEquals(true, pebbleGame.checkExit("e"));
        System.out.println("Test passed with input e!");
        Assert.assertEquals(true, pebbleGame.checkExit("E"));
        System.out.println("Test passed with input E!");
        Assert.assertEquals(false, pebbleGame.checkExit("notE"));
        System.out.println("Test passed with input notE!");
    }
    @Test
    public void testCreateFile() throws Exception{
        System.out.println("Testing creating a players file");
        pebbleGame.Player mockPlayer = new pebbleGame().new Player();
        Assert.assertNotNull(mockPlayer.createFile());
        System.out.println("Test passed!");
    }
    @Test
    public void testBag() throws Exception{
        System.out.println("Testing Bag methods");
        Bag testBag = new Bag();
        testBag.initPebbles();

        Bag testBag2 = new Bag();
        testBag2.initPebbles();

        System.out.println("Testing in() method");
        testBag.in(6);
        Assert.assertEquals(1,testBag.pebbles.size());
        System.out.println("Test passed!");

        System.out.println("Testing out() method");
        testBag.out();
        Assert.assertEquals(0,testBag.pebbles.size());
        System.out.println("Test passed!");

        testBag.pebbles = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15));
        int size = testBag.pebbles.size();

        System.out.println("Testing in() and out() method");
        for(int i = 0; i<size;i++) {
            System.out.println(testBag.pebbles);
            System.out.println(testBag2.pebbles);
            testBag2.in(testBag.out());
        }
        Assert.assertEquals(15,testBag2.pebbles.size());
        System.out.println("Test passed!");

        System.out.println("Testing paired bags");
        testBag.pair = testBag2;
        testBag2.pair = testBag;

        int take = testBag.out();
        Assert.assertEquals(14,testBag.pebbles.size());
        testBag.in(take);
        Assert.assertEquals(15,testBag.pebbles.size());
        System.out.println("Test passed!");

    }

    @Test
    public void testTake10() throws Exception{
        pebbleGame.Player mockPlayer = new pebbleGame().new Player();
        Bag testBag = new Bag();
        testBag.initPebbles();
        testBag.pebbles = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,15));
        ArrayList<Integer> duplicate = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12,13,15));
        System.out.println(testBag.pebbles);

        mockPlayer.hand = new ArrayList<Integer>();
        pebbleGame.blackBags.add(testBag);

        pebbleGame.blackBags.get(0).pebbles.addAll(testBag.pebbles);

        mockPlayer.hand = mockPlayer.take10(mockPlayer.hand,0);
        System.out.println(mockPlayer.hand);
        Assert.assertEquals(10,mockPlayer.hand.size());

    }
    @Test
    public void testNotFinished() throws Exception{
        pebbleGame.Player mockPlayer = new pebbleGame().new Player();
        mockPlayer.hand = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        mockPlayer.handSum = 0;
        Assert.assertTrue(mockPlayer.notFinished());

        mockPlayer.handSum = 0;
        mockPlayer.hand = new ArrayList<Integer>(Arrays.asList(10,10,10,10,10,10,10,10,10,10));
        Assert.assertFalse(mockPlayer.notFinished());

        mockPlayer.handSum = 0;
        mockPlayer.hand = new ArrayList<Integer>(Arrays.asList(10,10,10,10,10,10,10,10,10,10,0));
        Assert.assertTrue(mockPlayer.notFinished());
    }
    @Test
    public void testDraw() throws Exception{
        pebbleGame.Player mockPlayer = new pebbleGame().new Player();
        mockPlayer.hand = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));

        File file = new File("Player"+Long.toString(Thread.currentThread().getId()-10)+"output.txt");

        Bag testBag = new Bag();
        testBag.initPebbles();
        testBag.pebbles = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        pebbleGame.blackBags.add(testBag);

        System.out.println(mockPlayer.hand);
        mockPlayer.draw(mockPlayer.hand,file,0);
        Assert.assertTrue(mockPlayer.hand.size()==11);
        System.out.println(mockPlayer.hand);
    }
    @Test
    public void testDiscard() throws Exception{
        pebbleGame.Player mockPlayer = new pebbleGame().new Player();
        mockPlayer.hand = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));

        File file = new File("Player"+Long.toString(Thread.currentThread().getId()-10)+"output.txt");

        Bag testBag = new Bag();
        testBag.initPebbles();
        testBag.pebbles = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        pebbleGame.blackBags.add(testBag);

        Bag testWhiteBag = new Bag();
        testWhiteBag.initPebbles();
        testWhiteBag.pebbles = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));

        pebbleGame.blackBags.add(testBag);
        pebbleGame.whiteBags.add(testBag);

        System.out.println(mockPlayer.hand);
        mockPlayer.discard(mockPlayer.hand,file,0);
        Assert.assertTrue(mockPlayer.hand.size()==9);
        System.out.println(mockPlayer.hand);

    }
    @Test
    public void testPrintEndScore() throws Exception{
        pebbleGame.Player mockPlayer = new pebbleGame().new Player();
        pebbleGame.numPlayers = 2;
        Assert.assertTrue(mockPlayer.printEndScore("1","9","500"));
    }
}