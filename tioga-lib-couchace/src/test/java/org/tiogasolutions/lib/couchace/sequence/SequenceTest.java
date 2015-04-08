package org.tiogasolutions.lib.couchace.sequence;

import org.tiogasolutions.couchace.core.api.CouchException;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class SequenceTest {

  private SequenceType testType = new SequenceType() {
    @Override public String getCode() {
      return "test-thing";
    }
    @Override public String getId() {
      return "test-thing";
    }
    @Override public String getEntityType() {
      return "sequence-type";
    }
  };

  public void testCreate() throws Exception {
    Sequence sequence = null;
    try {
      new Sequence(testType, null);
      Assert.fail("Expected " + NullPointerException.class.getName());
    } catch (NullPointerException e) {
      Assert.assertEquals(e.getMessage(), "The value \"firstValue\" cannot be null.");
    }

    sequence = new Sequence(TestSequenceType.test, "2");
    Assert.assertEquals(sequence.getSequenceId(), "sequence-test");
    Assert.assertEquals(sequence.getLastValue(), "2");

    sequence = new Sequence(TestSequenceType.tinyUrl, "44");
    Assert.assertEquals(sequence.getSequenceId(), "sequence-tiny-url");
    Assert.assertEquals(sequence.getLastValue(), "44");
  }

  private void increment() throws Exception {
    Sequence sequence = new Sequence(testType, "");
    Assert.assertEquals(sequence.getLastValue(), "");

    Assert.assertEquals(sequence.incrementValue(), "0");

    Assert.assertEquals(sequence.incrementValue(), "0");
    Assert.assertEquals(sequence.incrementValue(), "1");
    Assert.assertEquals(sequence.incrementValue(), "2");
    Assert.assertEquals(sequence.incrementValue(), "3");
    Assert.assertEquals(sequence.incrementValue(), "4");
    Assert.assertEquals(sequence.incrementValue(), "5");
    Assert.assertEquals(sequence.incrementValue(), "6");
    Assert.assertEquals(sequence.incrementValue(), "7");
    Assert.assertEquals(sequence.incrementValue(), "8");
    Assert.assertEquals(sequence.incrementValue(), "9");

    Assert.assertEquals(sequence.incrementValue(), "a");
    Assert.assertEquals(sequence.incrementValue(), "b");
    Assert.assertEquals(sequence.incrementValue(), "c");
    Assert.assertEquals(sequence.incrementValue(), "d");
    Assert.assertEquals(sequence.incrementValue(), "e");
    Assert.assertEquals(sequence.incrementValue(), "f");
    Assert.assertEquals(sequence.incrementValue(), "g");
    Assert.assertEquals(sequence.incrementValue(), "h");
    Assert.assertEquals(sequence.incrementValue(), "i");
    Assert.assertEquals(sequence.incrementValue(), "j");
    Assert.assertEquals(sequence.incrementValue(), "k");
    Assert.assertEquals(sequence.incrementValue(), "l");
    Assert.assertEquals(sequence.incrementValue(), "m");
    Assert.assertEquals(sequence.incrementValue(), "n");
    Assert.assertEquals(sequence.incrementValue(), "o");
    Assert.assertEquals(sequence.incrementValue(), "p");
    Assert.assertEquals(sequence.incrementValue(), "q");
    Assert.assertEquals(sequence.incrementValue(), "r");
    Assert.assertEquals(sequence.incrementValue(), "s");
    Assert.assertEquals(sequence.incrementValue(), "t");
    Assert.assertEquals(sequence.incrementValue(), "u");
    Assert.assertEquals(sequence.incrementValue(), "v");
    Assert.assertEquals(sequence.incrementValue(), "w");
    Assert.assertEquals(sequence.incrementValue(), "x");
    Assert.assertEquals(sequence.incrementValue(), "y");
    Assert.assertEquals(sequence.incrementValue(), "z");

    Assert.assertEquals(sequence.incrementValue(), "A");
    Assert.assertEquals(sequence.incrementValue(), "B");
    Assert.assertEquals(sequence.incrementValue(), "C");
    Assert.assertEquals(sequence.incrementValue(), "D");
    Assert.assertEquals(sequence.incrementValue(), "E");
    Assert.assertEquals(sequence.incrementValue(), "F");
    Assert.assertEquals(sequence.incrementValue(), "G");
    Assert.assertEquals(sequence.incrementValue(), "H");
    Assert.assertEquals(sequence.incrementValue(), "I");
    Assert.assertEquals(sequence.incrementValue(), "J");
    Assert.assertEquals(sequence.incrementValue(), "K");
    Assert.assertEquals(sequence.incrementValue(), "L");
    Assert.assertEquals(sequence.incrementValue(), "M");
    Assert.assertEquals(sequence.incrementValue(), "N");
    Assert.assertEquals(sequence.incrementValue(), "O");
    Assert.assertEquals(sequence.incrementValue(), "P");
    Assert.assertEquals(sequence.incrementValue(), "Q");
    Assert.assertEquals(sequence.incrementValue(), "R");
    Assert.assertEquals(sequence.incrementValue(), "S");
    Assert.assertEquals(sequence.incrementValue(), "T");
    Assert.assertEquals(sequence.incrementValue(), "U");
    Assert.assertEquals(sequence.incrementValue(), "V");
    Assert.assertEquals(sequence.incrementValue(), "W");
    Assert.assertEquals(sequence.incrementValue(), "X");
    Assert.assertEquals(sequence.incrementValue(), "Y");
    Assert.assertEquals(sequence.incrementValue(), "Z");
  }

  public void testRollover() throws Exception {

    Sequence sequence = new Sequence(testType, "X");
    Assert.assertEquals(sequence.incrementValue(), "Y");
    Assert.assertEquals(sequence.incrementValue(), "Z");
    Assert.assertEquals(sequence.incrementValue(), "00");
    Assert.assertEquals(sequence.incrementValue(), "10");
    Assert.assertEquals(sequence.incrementValue(), "20");

    sequence = new Sequence(testType, "X0");
    Assert.assertEquals(sequence.incrementValue(), "Y0");
    Assert.assertEquals(sequence.incrementValue(), "Z0");
    Assert.assertEquals(sequence.incrementValue(), "01");
    Assert.assertEquals(sequence.incrementValue(), "11");
    Assert.assertEquals(sequence.incrementValue(), "21");

    sequence = new Sequence(testType, "XZ");
    Assert.assertEquals(sequence.incrementValue(), "YZ");
    Assert.assertEquals(sequence.incrementValue(), "ZZ");
    Assert.assertEquals(sequence.incrementValue(), "000");
    Assert.assertEquals(sequence.incrementValue(), "100");
    Assert.assertEquals(sequence.incrementValue(), "200");

    sequence = new Sequence(testType, "XZZ");
    Assert.assertEquals(sequence.incrementValue(), "YZZ");
    Assert.assertEquals(sequence.incrementValue(), "ZZZ");
    Assert.assertEquals(sequence.incrementValue(), "0000");
    Assert.assertEquals(sequence.incrementValue(), "1000");
    Assert.assertEquals(sequence.incrementValue(), "2000");
  }

  public void incrementChar() throws Exception {

    try {
      Sequence.incrementChar('@');
      Assert.fail("Expected exception");
    } catch (CouchException e) {
      Assert.assertEquals(e.getHttpStatusCode(), -1);
      Assert.assertEquals(e.getMessage(), "Cannot increment char for @.");
    }

    Assert.assertEquals(Sequence.incrementChar('0'), '1');
    Assert.assertEquals(Sequence.incrementChar('1'), '2');
    Assert.assertEquals(Sequence.incrementChar('2'), '3');
    Assert.assertEquals(Sequence.incrementChar('3'), '4');
    Assert.assertEquals(Sequence.incrementChar('4'), '5');
    Assert.assertEquals(Sequence.incrementChar('5'), '6');
    Assert.assertEquals(Sequence.incrementChar('6'), '7');
    Assert.assertEquals(Sequence.incrementChar('7'), '8');
    Assert.assertEquals(Sequence.incrementChar('8'), '9');
    Assert.assertEquals(Sequence.incrementChar('9'), 'a');
    
    Assert.assertEquals(Sequence.incrementChar('a'), 'b');
    Assert.assertEquals(Sequence.incrementChar('b'), 'c');
    Assert.assertEquals(Sequence.incrementChar('c'), 'd');
    Assert.assertEquals(Sequence.incrementChar('d'), 'e');
    Assert.assertEquals(Sequence.incrementChar('e'), 'f');
    Assert.assertEquals(Sequence.incrementChar('f'), 'g');
    Assert.assertEquals(Sequence.incrementChar('g'), 'h');
    Assert.assertEquals(Sequence.incrementChar('h'), 'i');
    Assert.assertEquals(Sequence.incrementChar('i'), 'j');
    Assert.assertEquals(Sequence.incrementChar('j'), 'k');
    Assert.assertEquals(Sequence.incrementChar('k'), 'l');
    Assert.assertEquals(Sequence.incrementChar('l'), 'm');
    Assert.assertEquals(Sequence.incrementChar('m'), 'n');
    Assert.assertEquals(Sequence.incrementChar('n'), 'o');
    Assert.assertEquals(Sequence.incrementChar('o'), 'p');
    Assert.assertEquals(Sequence.incrementChar('p'), 'q');
    Assert.assertEquals(Sequence.incrementChar('q'), 'r');
    Assert.assertEquals(Sequence.incrementChar('r'), 's');
    Assert.assertEquals(Sequence.incrementChar('s'), 't');
    Assert.assertEquals(Sequence.incrementChar('t'), 'u');
    Assert.assertEquals(Sequence.incrementChar('u'), 'v');
    Assert.assertEquals(Sequence.incrementChar('v'), 'w');
    Assert.assertEquals(Sequence.incrementChar('w'), 'x');
    Assert.assertEquals(Sequence.incrementChar('x'), 'y');
    Assert.assertEquals(Sequence.incrementChar('y'), 'z');
    Assert.assertEquals(Sequence.incrementChar('z'), 'A');
    
    Assert.assertEquals(Sequence.incrementChar('A'), 'B');
    Assert.assertEquals(Sequence.incrementChar('B'), 'C');
    Assert.assertEquals(Sequence.incrementChar('C'), 'D');
    Assert.assertEquals(Sequence.incrementChar('D'), 'E');
    Assert.assertEquals(Sequence.incrementChar('E'), 'F');
    Assert.assertEquals(Sequence.incrementChar('F'), 'G');
    Assert.assertEquals(Sequence.incrementChar('G'), 'H');
    Assert.assertEquals(Sequence.incrementChar('H'), 'I');
    Assert.assertEquals(Sequence.incrementChar('I'), 'J');
    Assert.assertEquals(Sequence.incrementChar('J'), 'K');
    Assert.assertEquals(Sequence.incrementChar('K'), 'L');
    Assert.assertEquals(Sequence.incrementChar('L'), 'M');
    Assert.assertEquals(Sequence.incrementChar('M'), 'N');
    Assert.assertEquals(Sequence.incrementChar('N'), 'O');
    Assert.assertEquals(Sequence.incrementChar('O'), 'P');
    Assert.assertEquals(Sequence.incrementChar('P'), 'Q');
    Assert.assertEquals(Sequence.incrementChar('Q'), 'R');
    Assert.assertEquals(Sequence.incrementChar('R'), 'S');
    Assert.assertEquals(Sequence.incrementChar('S'), 'T');
    Assert.assertEquals(Sequence.incrementChar('T'), 'U');
    Assert.assertEquals(Sequence.incrementChar('U'), 'V');
    Assert.assertEquals(Sequence.incrementChar('V'), 'W');
    Assert.assertEquals(Sequence.incrementChar('W'), 'X');
    Assert.assertEquals(Sequence.incrementChar('X'), 'Y');
    Assert.assertEquals(Sequence.incrementChar('Y'), 'Z');
    Assert.assertEquals(Sequence.incrementChar('Z'), '0');
  }
}
