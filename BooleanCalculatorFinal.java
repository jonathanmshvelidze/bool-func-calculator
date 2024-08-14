import java.util.*;
import java.io.*;
public class BooleanCalculatorFinal {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println();
    System.out.println("Welcome to the boolean expression calculator!\n");

    System.out.println("All POS expressions must be inputted with surrounding parenthesis in the format (A + B)(C) or (A+B)(C).");
    System.out.println("All SOP expressions must be inputted in the format A + AB' + AB.");
    System.out.println("Complement numbers are denoted by using \"'\". Enter \"000\" to quit anytime. Enjoy!");
    System.out.println();
    while (true) {
      try {
        System.out.print("Enter a boolean function: ");
        String functionString = scanner.nextLine();
        if (functionString.equals("000")) {
          break;
        }
        BooleanFunction function = stringToFunction(functionString);
        function.standardize();
        function.setKmap();
        function.solve();
        System.out.println(" ");
        System.out.print("Simplified Expression: ");
        System.out.println(function.solvedToString());
        System.out.println(" ");
        function.printTruthTable();
      } catch (Exception e) {
        System.out.println("EXCEPTION - " + e.getMessage());
      }
    }
  }
  public static BooleanFunction stringToFunction(String function) throws Exception {
    LinkedHashSet<Character> uniqueTerms = validateInput(function);
    boolean sopOrPos = true;
    if (function.charAt(0) == '(') {
      sopOrPos = false;
      String[] terms = function.split("(?=\\()");
      for (int i = 0; i < terms.length; i++) {
              terms[i] = terms[i].substring(1 , terms[i].length() - 1);
      }
      return new BooleanFunction(terms, sopOrPos, uniqueTerms);
    } else {
      String[] terms = function.split("\\+");
      for (int i = 0; i < terms.length; i++) {
              terms[i] = terms[i].trim();
      }
      return new BooleanFunction(terms, sopOrPos, uniqueTerms);
    }
  }
  public static LinkedHashSet<Character> validateInput(String function) throws Exception {
    LinkedHashSet<Character> uniqueTerms = new LinkedHashSet<Character>();
    for (int i = 0; i < function.length(); i++) {
        if (Character.isLetter(function.charAt(i)) || function.charAt(i) == '+' || function.charAt(i) == '\''
        || function.charAt(i) == '(' || function.charAt(i) == ')' || function.charAt(i) == ' ') {
          if (Character.isLetter(function.charAt(i))) {
            uniqueTerms.add(function.charAt(i));
          }
        } else {
          throw new Exception ("Invalid input: Acceptable characters are letters A - Z, + signs, and \'");
        }
    }
    if (uniqueTerms.size() < 2 || uniqueTerms.size() > 4) {
      throw new Exception("Invalid input: Boolean function must contain between 2 and 4 terms");
    }
    return uniqueTerms;
  }
}
class BooleanFunction {
  private String[] terms;
  private ArrayList<String> numerifiedTerms = new ArrayList<String>();
  private boolean sopOrPos;
  private LinkedHashSet<Character> uniqueTerms;
  private int[] kmap;
  private ArrayList<String> solved = new ArrayList<String>();
  private final String[] twoTermReference = {"00", "01", "10", "11"};
  private final String[] threeTermReference = {"000", "001", "010", "011", "100", "101", "110", "111"};
  private final String[] fourTermReference = {"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};

  public BooleanFunction(String[] terms, boolean sopOrPos, LinkedHashSet<Character> uniqueTerms) {
    this.terms = terms;
    this.sopOrPos = sopOrPos;
    this.uniqueTerms = uniqueTerms;
    this.kmap = new int[(int)Math.pow(2, uniqueTerms.size())];
  }
  public int getTermCount() {
    return uniqueTerms.size();
  }
  public void printTerms() {
    int i;
    for (i = 0; i < terms.length - 1; i++) {
      System.out.print(terms[i] + ", ");
    }
    System.out.println(terms[i]);
  }
  public void printNumerified() {
    System.out.println(numerifiedTerms.toString());
  }
  public boolean getSopOrPos() {
    return sopOrPos;
  }
  public String[] getTerms() {
    return terms;
  }
  public int getKmapSize() {
    return kmap.length;
  }
  public void printKmap() {
    for (int i = 0; i < kmap.length; i++) {
      System.out.print(kmap[i] + " ");
    }
    System.out.println("");
  }
  public void standardize() {
    if (!sopOrPos) {
      for (int i = 0; i < terms.length; i++) {
        if (terms[i].contains(" + ")) {
          terms[i] = terms[i].replace(" + ", "");
        }
        if (terms[i].contains("+")) {
          terms[i] = terms[i].replace("+", "");
        }
      }
    }
    ArrayList<String> standardized = new ArrayList<String>();
      for (int i = 0; i < terms.length; i++) {
          String currentTerm = terms[i];
          int correctionMark = 0;
          ArrayList<String> termsMissing = new ArrayList<String>(uniqueTerms.size());
          for (Character uniqueChar : uniqueTerms) {
                if (!currentTerm.contains(uniqueChar.toString())) {
                    correctionMark += 1;
                    termsMissing.add(uniqueChar.toString());
                }
          }
          if (correctionMark == 3) {
            standardized.add(currentTerm + termsMissing.get(0) + termsMissing.get(1) + termsMissing.get(2));
            standardized.add(currentTerm + termsMissing.get(0) + termsMissing.get(1) + termsMissing.get(2) + "'");
            standardized.add(currentTerm + termsMissing.get(0) + termsMissing.get(1) + "'" + termsMissing.get(2));
            standardized.add(currentTerm + termsMissing.get(0) + termsMissing.get(1) + "'" + termsMissing.get(2) + "'");
            standardized.add(currentTerm + termsMissing.get(0) + "'" + termsMissing.get(1) + termsMissing.get(2));
            standardized.add(currentTerm + termsMissing.get(0) + "'" + termsMissing.get(1) + termsMissing.get(2) + "'");
            standardized.add(currentTerm + termsMissing.get(0) + "'" + termsMissing.get(1) + "'" + termsMissing.get(2));
            standardized.add(currentTerm + termsMissing.get(0) + "'" + termsMissing.get(1) + "'" + termsMissing.get(2) + "'");
          }
          if (correctionMark == 2) {
            standardized.add(currentTerm + termsMissing.get(0) + termsMissing.get(1));
            standardized.add(currentTerm + termsMissing.get(0) + termsMissing.get(1) + "'");
            standardized.add(currentTerm + termsMissing.get(0) + "'" + termsMissing.get(1));
            standardized.add(currentTerm + termsMissing.get(0) + "'" + termsMissing.get(1) + "'");
          }
          if (correctionMark == 1) {
            standardized.add(currentTerm + termsMissing.get(0));
            standardized.add(currentTerm + termsMissing.get(0) + "'");
          }
          if (correctionMark == 0) {
            standardized.add(currentTerm);
          }

          }
          this.terms = standardized.toArray(terms);
          this.shuffle();
  }
  public void setKmap() {
    if (sopOrPos) {
        Arrays.fill(kmap, 0);
    } else {
        Arrays.fill(kmap, 1);
    }
    for (String numerifiedTerm : numerifiedTerms) {
        if (uniqueTerms.size() == 2) {
            for (int j = 0; j < twoTermReference.length; j++) {
                if (numerifiedTerm.equals(twoTermReference[j])) {
                    if (sopOrPos) {
                      kmap[j] = 1;
                      break;
                    } else {
                      kmap[j] = 0;
                      break;
                    }
                }
            }
        } else if (uniqueTerms.size() == 3) {
            for (int j = 0; j < threeTermReference.length; j++) {
                if (numerifiedTerm.equals(threeTermReference[j])) {
                  if (sopOrPos) {
                    kmap[j] = 1;
                    break;
                  } else {
                    kmap[j] = 0;
                    break;
                  }
                }
            }
        } else if (uniqueTerms.size() == 4) {
            for (int j = 0; j < fourTermReference.length; j++) {
                if (numerifiedTerm.equals(fourTermReference[j])) {
                  if (sopOrPos) {
                    kmap[j] = 1;
                    break;
                  } else {
                    kmap[j] = 0;
                    break;
                  }
                }
            }
        }
    }
  }
  public void shuffle() {
    for (int i = 0; i < terms.length; i++) {
      terms[i] = termShuffle(terms[i]);
      numerifiedTerms.add(numerify(terms[i]));
    }
  }
  public String termShuffle(String term) {
    ArrayList<String> splitTerms = new ArrayList<String>();
      for (int i = 0; i < term.length(); i++) {
        if (i < term.length() - 1 && term.charAt(i + 1) == '\'') {
          splitTerms.add(term.charAt(i) + "'");
          i++;
        } else {
          splitTerms.add(Character.toString(term.charAt(i)));
        }
      }
      ArrayList<String> shuffledTerms = new ArrayList<>();
        for (Character uniqueChar : uniqueTerms) {
            String uniqueTerm = uniqueChar.toString();
            for (String splitTerm : splitTerms) {
                if (splitTerm.startsWith(uniqueTerm)) {
                    shuffledTerms.add(splitTerm);
                    break;
                }
            }
        }
      String result = "";
      for (int i = 0; i < shuffledTerms.size(); i++) {
          result += shuffledTerms.get(i);
      }
      return result;
  }
  public void printTruthTable() {
    Character[] uniqueArray = uniqueTerms.toArray(new Character[0]);
    int i;
    System.out.println("Truth Table: \n");
    for (i = 0; i < uniqueArray.length; i++) {
      System.out.print(uniqueArray[i] + "  |  ");
    }
    System.out.println(this.solvedToString());
    System.out.print("----------------------");
    for (int z = 0; z < this.solvedToString().length() + 3; z++) {
      System.out.print("-");
    }
    System.out.println("-");
    for (int j = 0; j < kmap.length; j++) {
      if (uniqueArray.length == 2) {
        System.out.println(twoTermReference[j].charAt(0) + "  |  " + twoTermReference[j].charAt(1) + "  |  " + kmap[j]);
      }
      if (uniqueArray.length == 3) {
        System.out.println(threeTermReference[j].charAt(0) + "  |  " +
        threeTermReference[j].charAt(1) + "  |  " + threeTermReference[j].charAt(2) + "  |  " + kmap[j]);

      }
      if (uniqueArray.length == 4) {
        System.out.println(fourTermReference[j].charAt(0) + "  |  " + fourTermReference[j].charAt(1) + "  |  " +
        fourTermReference[j].charAt(2) + "  |  " + fourTermReference[j].charAt(3) + "  |  " + kmap[j]);
      }
    }
    System.out.println("");
  }
  public String numerify(String term) {
    String number = "";
    for (int i = 0; i < term.length(); i++) {
      if (i < term.length() - 1 && term.charAt(i + 1) == '\'') {
        if (sopOrPos) {
          number += "0";
          i++;
        } else {
          number += "1";
          i++;
        }
      } else {
        if (sopOrPos) {
          number += "1";
        } else {
          number += "0";
        }
      }
    }
    return number;
  }
  public void solve() {
    if (uniqueTerms.size() == 2) {
      twoInputSolve();
    }
    if (uniqueTerms.size() == 3) {
      threeInputSolve();
    }
    if (uniqueTerms.size() == 4) {
      fourInputSolve();
    }
  }
  public void checkRedundancies(ArrayList<String> solved, ArrayList<String> checker, int[] redundancyMarker) {
    for (int i = checker.size() - 1; i >= 0; i--) {
      String[] splitGroupTerms = checker.get(i).split(" ");
      if (splitGroupTerms.length == 2) {
        if(!checkTwoOverlap(redundancyMarker, Integer.parseInt(splitGroupTerms[0]), Integer.parseInt(splitGroupTerms[1]))) {
          solved.remove(i);
        }
      }
      if (splitGroupTerms.length == 4) {
        if(!checkFourOverlap(redundancyMarker, Integer.parseInt(splitGroupTerms[0]), Integer.parseInt(splitGroupTerms[1]), Integer.parseInt(splitGroupTerms[2]), Integer.parseInt(splitGroupTerms[3]))) {
          solved.remove(i);
        }
      }
      if (splitGroupTerms.length == 8) {
        if(!checkEightOverlap(redundancyMarker, Integer.parseInt(splitGroupTerms[0]), Integer.parseInt(splitGroupTerms[1]), Integer.parseInt(splitGroupTerms[2]), Integer.parseInt(splitGroupTerms[3]), Integer.parseInt(splitGroupTerms[4]), Integer.parseInt(splitGroupTerms[5]), Integer.parseInt(splitGroupTerms[6]), Integer.parseInt(splitGroupTerms[7]))) {
          solved.remove(i);
        }
      }
    }
  }
  public void twoInputSolve() {
    int[] marker = new int[kmap.length];
    int[] redundancyMarker = new int[kmap.length];
    Character[] uniqueArray = uniqueTerms.toArray(new Character[0]);
    ArrayList<String> checker = new ArrayList<String>();
    Arrays.fill(marker, 0);
    Arrays.fill(redundancyMarker, 0);
    if (sopOrPos) {
      if ((kmap[0] == 1 && kmap[1] == 1 && kmap[2] == 1 && kmap[3] == 1)) {
        solved.add("1");
        Arrays.fill(marker, 2);
      }
      if (checkTwo(marker, 0, 1)) {
        solved.add(uniqueArray[0].toString() + "'");
        checker.add("0 1");
      }
      if (checkTwo(marker, 2, 3)) {
        solved.add(uniqueArray[0].toString());
        checker.add("2 3");
      }
      if (checkTwo(marker, 0, 2)) {
        solved.add(uniqueArray[1].toString() + "'");
        checker.add("0 2");
      }
      if (checkTwo(marker, 1, 3)) {
        solved.add(uniqueArray[1].toString());
        checker.add("1 3");
      }

      if (checkTwoOverlap(marker, 0, 1)) {
        solved.add(uniqueArray[0].toString() + "'");
        checker.add("0 1");
      }
      if (checkTwoOverlap(marker, 2, 3)) {
        solved.add(uniqueArray[0].toString());
        checker.add("2 3");
      }
      if (checkTwoOverlap(marker, 0, 2)) {
        solved.add(uniqueArray[1].toString() + "'");
        checker.add("0 2");
      }
      if (checkTwoOverlap(marker, 1, 3)) {
        solved.add(uniqueArray[1].toString());
        checker.add("1 3");
      }


      if (checkSingle(marker, 0)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'");
      }
      if (checkSingle(marker, 1)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString());
      }
      if (checkSingle(marker, 2)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'");
      }
      if (checkSingle(marker, 3)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString());
      }

      boolean noInput = true;
      for (int element : marker) {
        if (element != 0) {
          noInput = false;
        }
      }
      if (noInput) {
        solved.add("0");
      }
    } else {
      if ((kmap[0] == 0 && kmap[1] == 0 && kmap[2] == 0 && kmap[3] == 0)) {
        solved.add("0");
        Arrays.fill(marker, 2);
      }
      if (checkTwo(marker, 0, 1)) {
        solved.add("(" + uniqueArray[0].toString() + ")");
        checker.add("0 1");
      }
      if (checkTwo(marker, 2, 3)) {
        solved.add("(" + uniqueArray[0].toString() + "')");
        checker.add("2 3");
      }
      if (checkTwo(marker, 0, 2)) {
        solved.add("(" + uniqueArray[1].toString() + ")");
        checker.add("0 2");
      }
      if (checkTwo(marker, 1, 3)) {
        solved.add("(" + uniqueArray[1].toString() + "')");
        checker.add("1 3");
      }

      if (checkTwoOverlap(marker, 0, 1)) {
        solved.add("(" + uniqueArray[0].toString() + ")");
        checker.add("0 1");
      }
      if (checkTwoOverlap(marker, 2, 3)) {
        solved.add("(" + uniqueArray[0].toString() + "')");
        checker.add("2 3");
      }
      if (checkTwoOverlap(marker, 0, 2)) {
        solved.add("(" + uniqueArray[1].toString() + ")");
        checker.add("0 2");
      }
      if (checkTwoOverlap(marker, 1, 3)) {
        solved.add("(" + uniqueArray[1].toString() + "')");
        checker.add("1 3");
      }


      if (checkSingle(marker, 0)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + ")");
      }
      if (checkSingle(marker, 1)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + "')");
      }
      if (checkSingle(marker, 2)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + ")");
      }
      if (checkSingle(marker, 3)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + "')");
      }
      boolean noInput = true;
      for (int element : marker) {
        if (element != 0) {
          noInput = false;
        }
      }
      if (noInput) {
        solved.add("1");
      }
    }
    checkRedundancies(solved, checker, redundancyMarker);
  }
  public void threeInputSolve() {
    int[] marker = new int[kmap.length];
    int[] redundancyMarker = new int[kmap.length];
    Character[] uniqueArray = uniqueTerms.toArray(new Character[0]);
    ArrayList<String> checker = new ArrayList<String>();
    Arrays.fill(marker, 0);
    Arrays.fill(redundancyMarker, 0);
    if (sopOrPos) {
      if (kmap[0] == 1 && kmap[1] == 1 && kmap[2] == 1 && kmap[3] == 1 && kmap[4] == 1
      && kmap[5] == 1 && kmap[6] == 1 && kmap[7] == 1) {
        solved.add("1");
        Arrays.fill(marker, 2);
      }
      if (checkFour(marker, 0, 1, 2, 3)) {
        solved.add(uniqueArray[0].toString() + "'");
        checker.add("0 1 2 3");
      }
      if (checkFour(marker, 4, 5, 6, 7)) {
        solved.add(uniqueArray[0].toString());
        checker.add("4 5 6 7");
      }
      if (checkFour(marker, 0, 1, 4, 5)) {
        solved.add(uniqueArray[1].toString() + "'");
        checker.add("0 1 4 5");
      }
      if (checkFour(marker, 1, 3, 5, 7)) {
        solved.add(uniqueArray[2].toString());
        checker.add("1 3 5 7");
      }
      if (checkFour(marker, 3, 2, 7, 6)) {
        solved.add(uniqueArray[1].toString());
        checker.add("3 2 7 6");
      }
      if (checkFour(marker, 0, 2, 4, 6)) {
        solved.add(uniqueArray[2].toString() + "'");
        checker.add("0 2 4 6");
      }



      if (checkFourOverlap(marker, 0, 1, 2, 3)) {
        solved.add(uniqueArray[0].toString() + "'");
        checker.add("0 1 2 3");
      }
      if (checkFourOverlap(marker, 4, 5, 6, 7)) {
        solved.add(uniqueArray[0].toString());
        checker.add("4 5 6 7");
      }
      if (checkFourOverlap(marker, 0, 1, 4, 5)) {
        solved.add(uniqueArray[1].toString() + "'");
        checker.add("0 1 4 5");
      }
      if (checkFourOverlap(marker, 1, 3, 5, 7)) {
        solved.add(uniqueArray[2].toString());
        checker.add("1 3 5 7");
      }
      if (checkFourOverlap(marker, 3, 2, 7, 6)) {
        solved.add(uniqueArray[1].toString());
        checker.add("3 2 7 6");
      }
      if (checkFourOverlap(marker, 0, 2, 4, 6)) {
        solved.add(uniqueArray[2].toString() + "'");
        checker.add("0 2 4 6");
      }





      if (checkTwo(marker, 0, 1)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("0 1");
      }
      if (checkTwo(marker, 1, 3)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString());
        checker.add("1 3");
      }
      if (checkTwo(marker, 3, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString());
        checker.add("3 2");
      }
      if (checkTwo(marker, 0, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 2");
      }
      if (checkTwo(marker, 4, 5)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'");
        checker.add("4 5");
      }
      if (checkTwo(marker, 5, 7)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString());
        checker.add("5 7");
      }
      if (checkTwo(marker, 7, 6)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString());
        checker.add("7 6");
      }
      if (checkTwo(marker, 4, 6)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString() + "'");
        checker.add("4 6");
      }
      if (checkTwo(marker, 0, 4)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 4");
      }
      if (checkTwo(marker, 1, 5)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("1 5");
      }
      if (checkTwo(marker, 3, 7)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("3 7");
      }
      if (checkTwo(marker, 2, 6)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("2 6");
      }





      if (checkTwoOverlap(marker, 0, 1)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("0 1");
      }
      if (checkTwoOverlap(marker, 1, 3)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString());
        checker.add("1 3");
      }
      if (checkTwoOverlap(marker, 3, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString());
        checker.add("3 2");
      }
      if (checkTwoOverlap(marker, 0, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 2");
      }
      if (checkTwoOverlap(marker, 4, 5)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'");
        checker.add("4 5");
      }
      if (checkTwoOverlap(marker, 5, 7)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString());
        checker.add("5 7");
      }
      if (checkTwoOverlap(marker, 7, 6)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString());
        checker.add("7 6");
      }
      if (checkTwoOverlap(marker, 4, 6)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString() + "'");
        checker.add("4 6");
      }
      if (checkTwoOverlap(marker, 0, 4)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 4");
      }
      if (checkTwoOverlap(marker, 1, 5)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("1 5");
      }
      if (checkTwoOverlap(marker, 3, 7)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("3 7");
      }
      if (checkTwoOverlap(marker, 2, 6)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("2 6");
      }



      if (checkSingle(marker, 0)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
      }
      if (checkSingle(marker, 1)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
      }
      if (checkSingle(marker, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
      }
      if (checkSingle(marker, 3)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString());
      }
      if (checkSingle(marker, 4)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
      }
      if (checkSingle(marker, 5)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
      }
      if (checkSingle(marker, 6)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
      }
      if (checkSingle(marker, 7)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString());
      }
      boolean noInput = true;
      for (int element : marker) {
        if (element != 0) {
          noInput = false;
        }
      }
      if (noInput) {
        solved.add("0");
      }
    } else {
      if (kmap[0] == 0 && kmap[1] == 0 && kmap[2] == 0 && kmap[3] == 0 && kmap[4] == 0
      && kmap[5] == 0 && kmap[6] == 0 && kmap[7] == 0) {
        solved.add("0");
        Arrays.fill(marker, 2);
      }
      if (checkFour(marker, 0, 1, 2, 3)) {
        solved.add("(" + uniqueArray[0].toString() + ")");
        checker.add("0 1 2 3");
      }
      if (checkFour(marker, 4, 5, 6, 7)) {
        solved.add("(" + uniqueArray[0].toString() + "')");
        checker.add("4 5 6 7");
      }
      if (checkFour(marker, 0, 1, 4, 5)) {
        solved.add("(" + uniqueArray[1].toString() + ")");
        checker.add("0 1 4 5");
      }
      if (checkFour(marker, 1, 3, 5, 7)) {
        solved.add("(" + uniqueArray[2].toString() + "')");
        checker.add("1 3 5 7");
      }
      if (checkFour(marker, 3, 2, 7, 6)) {
        solved.add("(" + uniqueArray[1].toString() + "')");
        checker.add("3 2 7 6");
      }
      if (checkFour(marker, 0, 2, 4, 6)) {
        solved.add("(" + uniqueArray[2].toString() + ")");
        checker.add("0 2 4 6");
      }




      if (checkFourOverlap(marker, 0, 1, 2, 3)) {
        solved.add("(" + uniqueArray[0].toString() + ")");
        checker.add("0 1 2 3");
      }
      if (checkFourOverlap(marker, 4, 5, 6, 7)) {
        solved.add("(" + uniqueArray[0].toString() + "')");
        checker.add("4 5 6 7");
      }
      if (checkFourOverlap(marker, 0, 1, 4, 5)) {
        solved.add("(" + uniqueArray[1].toString() + ")");
        checker.add("0 1 4 5");
      }
      if (checkFourOverlap(marker, 1, 3, 5, 7)) {
        solved.add("(" + uniqueArray[2].toString() + "')");
        checker.add("1 3 5 7");
      }
      if (checkFourOverlap(marker, 3, 2, 7, 6)) {
        solved.add("(" + uniqueArray[1].toString() + "')");
        checker.add("3 2 7 6");
      }
      if (checkFourOverlap(marker, 0, 2, 4, 6)) {
        solved.add("(" + uniqueArray[2].toString() + ")");
        checker.add("0 2 4 6");
      }










      if (checkTwo(marker, 0, 1)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("0 1");
      }
      if (checkTwo(marker, 1, 3)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString() + "')");
        checker.add("1 3");
      }
      if (checkTwo(marker, 3, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + "')");
        checker.add("3 2");
      }
      if (checkTwo(marker, 0, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 2");
      }
      if (checkTwo(marker, 4, 5)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + ")");
        checker.add("4 5");
      }
      if (checkTwo(marker, 5, 7)) {
        solved.add("(" + uniqueArray[0].toString() + "' + "+ uniqueArray[2].toString() + "')");
        checker.add("5 7");
      }
      if (checkTwo(marker, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + "' + "+ uniqueArray[1].toString() + "')");
        checker.add("7 6");
      }
      if (checkTwo(marker, 4, 6)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[2].toString() + ")");
        checker.add("4 6");
      }
      if (checkTwo(marker, 0, 4)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 4");
      }
      if (checkTwo(marker, 1, 5)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + "')");
        checker.add("1 5");
      }
      if (checkTwo(marker, 3, 7)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + "')");
        checker.add("3 7");
      }
      if (checkTwo(marker, 2, 6)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + ")");
        checker.add("2 6");
      }





      if (checkTwoOverlap(marker, 0, 1)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("0 1");
      }
      if (checkTwoOverlap(marker, 1, 3)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString() + "')");
        checker.add("1 3");
      }
      if (checkTwoOverlap(marker, 3, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + "')");
        checker.add("3 2");
      }
      if (checkTwoOverlap(marker, 0, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 2");
      }
      if (checkTwoOverlap(marker, 4, 5)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + ")");
        checker.add("4 5");
      }
      if (checkTwoOverlap(marker, 5, 7)) {
        solved.add("(" + uniqueArray[0].toString() + "' + "+ uniqueArray[2].toString() + "')");
        checker.add("5 7");
      }
      if (checkTwoOverlap(marker, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + "' + "+ uniqueArray[1].toString() + "')");
        checker.add("7 6");
      }
      if (checkTwoOverlap(marker, 4, 6)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[2].toString() + ")");
        checker.add("4 6");
      }
      if (checkTwoOverlap(marker, 0, 4)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 4");
      }
      if (checkTwoOverlap(marker, 1, 5)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + "')");
        checker.add("1 5");
      }
      if (checkTwoOverlap(marker, 3, 7)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + "')");
        checker.add("3 7");
      }
      if (checkTwoOverlap(marker, 2, 6)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + ")");
        checker.add("2 6");
      }





      if (checkSingle(marker, 0)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
      }
      if (checkSingle(marker, 1)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + "')");
      }
      if (checkSingle(marker, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + ")");
      }
      if (checkSingle(marker, 3)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + "')");
      }
      if (checkSingle(marker, 4)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
      }
      if (checkSingle(marker, 5)) {
        solved.add("('" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + "')");
      }
      if (checkSingle(marker, 6)) {
        solved.add("('" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + ")");
      }
      if (checkSingle(marker, 7)) {
        solved.add("(" + uniqueArray[0].toString() + "' + " + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + "')");
      }
      boolean noInput = true;
      for (int element : marker) {
        if (element != 0) {
          noInput = false;
        }
      }
      if (noInput) {
        solved.add("1");
      }
    }
    checkRedundancies(solved, checker, redundancyMarker);
  }
  public void fourInputSolve() {
    int[] marker = new int[kmap.length];
    int[] redundancyMarker = new int[kmap.length];
    Character[] uniqueArray = uniqueTerms.toArray(new Character[0]);
    ArrayList<String> checker = new ArrayList<String>();
    Arrays.fill(marker, 0);
    Arrays.fill(redundancyMarker, 0);
    if (sopOrPos) {
      if (kmap[0] == 1 && kmap[1] == 1 && kmap[2] == 1 && kmap[3] == 1 && kmap[4] == 1 && kmap[5] == 1 &&
      kmap[6] == 1 && kmap[7] == 1 && kmap[8] == 1 && kmap[9] == 1 &&kmap[10] == 1 &&
      kmap[11] == 1 && kmap[12] == 1 && kmap[13] == 1 && kmap[14] == 1 && kmap[15] == 1) {
        solved.add("1");
        Arrays.fill(marker, 2);
      }
      if (checkEight(marker, 0, 1, 2, 3, 4, 5, 6, 7)) {
        solved.add(uniqueArray[0].toString() + "'");
        checker.add("0 1 2 3 4 5 6 7");
      }
      if (checkEight(marker, 12, 13, 15, 14, 8, 9, 11, 10)) {
        solved.add(uniqueArray[0].toString());
        checker.add("12 13 15 14 8 9 11 10");

      }
      if (checkEight(marker, 0, 1, 4, 5, 12, 13, 8, 9)) {
        solved.add(uniqueArray[2].toString() + "'");
        checker.add("0 1 4 5 12 13 8 9");
      }
      if (checkEight(marker, 3, 2, 7, 6, 15, 14, 11, 10)) {
        solved.add(uniqueArray[2].toString());
        checker.add("3 2 7 6 15 14 11 10");
      }
      if (checkEight(marker, 1, 3, 5, 7, 13, 15, 9, 11)) {
        solved.add(uniqueArray[3].toString());
        checker.add("1 3 5 7 13 15 9 11");
      }
      if (checkEight(marker, 0, 4, 12, 8, 2, 6, 14, 10)) {
        solved.add(uniqueArray[3].toString() + "'");
        checker.add("0 4 12 8 2 6 14 10");
      }
      if (checkEight(marker, 4, 5, 7, 6, 12, 13, 15, 14)) {
        solved.add(uniqueArray[1].toString());
        checker.add("4 5 7 6 12 13 15 14");
      }
      if (checkEight(marker, 0, 1, 3, 2, 8, 9, 11, 10)) {
        solved.add(uniqueArray[1].toString() + "'");
        checker.add("0 1 3 2 8 9 11 10");
      }




      if (checkEightOverlap(marker, 0, 1, 2, 3, 4, 5, 6, 7)) {
        solved.add(uniqueArray[0].toString() + "'");
        checker.add("0 1 2 3 4 5 6 7");
      }
      if (checkEightOverlap(marker, 12, 13, 15, 14, 8, 9, 11, 10)) {
        solved.add(uniqueArray[0].toString());
        checker.add("12 13 15 14 8 9 10 11");
      }
      if (checkEightOverlap(marker, 0, 1, 4, 5, 12, 13, 8, 9)) {
        solved.add(uniqueArray[2].toString() + "'");
        checker.add("0 1 4 5 12 13 8 9");
      }
      if (checkEightOverlap(marker, 3, 2, 7, 6, 15, 14, 11, 10)) {
        solved.add(uniqueArray[2].toString());
        checker.add("3 2 7 6 15 14 11 10");
      }
      if (checkEightOverlap(marker, 1, 3, 5, 7, 13, 15, 9, 11)) {
        solved.add(uniqueArray[3].toString());
        checker.add("1 3 5 7 13 15 9 11");
      }
      if (checkEightOverlap(marker, 0, 4, 12, 8, 2, 6, 14, 10)) {
        solved.add(uniqueArray[3].toString() + "'");
        checker.add("0 4 12 8 2 6 14 10");
      }
      if (checkEightOverlap(marker, 4, 5, 7, 6, 12, 13, 15, 14)) {
        solved.add(uniqueArray[1].toString());
        checker.add("4 5 7 6 12 13 15 14");
      }
      if (checkEightOverlap(marker, 0, 1, 3, 2, 8, 9, 11, 10)) {
        solved.add(uniqueArray[1].toString() + "'");
        checker.add("0 1 3 2 8 9 11 10");
      }






      if (checkFour(marker, 0, 1, 3, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("0 1 3 2");
      }
      if (checkFour(marker, 4, 5, 7, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString());
        checker.add("4 5 7 6");
      }
      if (checkFour(marker, 12, 13, 15, 14)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString());
        checker.add("12 13 15 14");
      }
      if (checkFour(marker, 8, 9, 11, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'");
        checker.add("8 9 11 10");
      }
      if (checkFour(marker, 0, 2, 8, 10)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 2 8 10");
      }
      if (checkFour(marker, 0, 1, 8, 9)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 1 8 9");
      }
      if (checkFour(marker, 1, 3, 9, 11)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 3 9 11");
      }
      if (checkFour(marker, 3, 2, 11, 10)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("3 2 11 10");
      }
      if (checkFour(marker, 0, 4, 2, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 4 2 6");
      }
      if (checkFour(marker, 4, 6, 12, 14)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[3].toString() + "'");
        checker.add("4 6 12 14");
      }
      if (checkFour(marker, 12, 14, 8, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[3].toString() + "'");
        checker.add("12 14 8 10");
      }
      if (checkFour(marker, 0, 1, 4, 5)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 1 4 5");
      }
      if (checkFour(marker, 1, 3, 5, 7)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 3 5 7");
      }
      if (checkFour(marker, 3, 2, 7, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString());
        checker.add("3 2 7 6");
      }
      if (checkFour(marker, 4, 5, 12, 13)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("4 5 12 13");
      }
      if (checkFour(marker, 5, 7, 13, 15)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[3].toString());
        checker.add("5 7 13 15");
      }
      if (checkFour(marker, 7, 6, 15, 14)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("7 6 15 14");
      }
      if (checkFour(marker, 12, 13, 8, 9)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString() + "'");
        checker.add("12 13 8 9");
      }
      if (checkFour(marker, 13, 15, 9, 11)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[3].toString());
        checker.add("13 15 9 11");
      }
      if (checkFour(marker, 15, 14, 11, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString());
        checker.add("15 14 11 10");
      }
      if (checkFour(marker, 0, 4, 12, 8)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 4 12 8");
      }
      if (checkFour(marker, 1, 5, 13, 9)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 5 13 9");
      }
      if (checkFour(marker, 3, 7, 15, 11)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString());
        checker.add("3 7 15 11");
      }
      if (checkFour(marker, 2, 6, 14, 10)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'");
        checker.add("2 6 14 10");
      }





      if (checkFourOverlap(marker, 0, 1, 3, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("0 1 3 2");
      }
      if (checkFourOverlap(marker, 4, 5, 7, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString());
        checker.add("4 5 7 6");
      }
      if (checkFourOverlap(marker, 12, 13, 15, 14)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString());
        checker.add("12 13 15 14");
      }
      if (checkFourOverlap(marker, 8, 9, 11, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'");
        checker.add("8 9 11 10");
      }
      if (checkFourOverlap(marker, 0, 2, 8, 10)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 2 8 10");
      }
      if (checkFourOverlap(marker, 0, 1, 8, 9)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 1 8 9");
      }
      if (checkFourOverlap(marker, 1, 3, 9, 11)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 3 9 11");
      }
      if (checkFourOverlap(marker, 3, 2, 11, 10)) {
        solved.add(uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("3 2 11 10");
      }
      if (checkFourOverlap(marker, 0, 4, 2, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 4 2 6");
      }
      if (checkFourOverlap(marker, 4, 6, 12, 14)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[3].toString() + "'");
        checker.add("4 6 12 14");
      }
      if (checkFourOverlap(marker, 12, 14, 8, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[3].toString() + "'");
        checker.add("12 14 8 10");
      }
      if (checkFourOverlap(marker, 0, 1, 4, 5)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 1 4 5");
      }
      if (checkFourOverlap(marker, 1, 3, 5, 7)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 3 5 7");
      }
      if (checkFourOverlap(marker, 3, 2, 7, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[2].toString());
        checker.add("3 2 7 6");
      }
      if (checkFourOverlap(marker, 4, 5, 12, 13)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("4 5 12 13");
      }
      if (checkFourOverlap(marker, 5, 7, 13, 15)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[3].toString());
        checker.add("5 7 13 15");
      }
      if (checkFourOverlap(marker, 7, 6, 15, 14)) {
        solved.add(uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("7 6 15 14");
      }
      if (checkFourOverlap(marker, 12, 13, 8, 9)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString() + "'");
        checker.add("12 13 8 9");
      }
      if (checkFourOverlap(marker, 13, 15, 9, 11)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[3].toString());
        checker.add("13 15 9 11");
      }
      if (checkFourOverlap(marker, 15, 14, 11, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[2].toString());
        checker.add("15 14 11 10");
      }
      if (checkFourOverlap(marker, 0, 4, 12, 8)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 4 12 8");
      }
      if (checkFourOverlap(marker, 1, 5, 13, 9)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 5 13 9");
      }
      if (checkFourOverlap(marker, 3, 7, 15, 11)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString());
        checker.add("3 7 15 11");
      }
      if (checkFourOverlap(marker, 2, 6, 14, 10)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'");
        checker.add("2 6 14 10");
      }










      if (checkTwo(marker, 0, 1)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 1");
      }
      if (checkTwo(marker, 1, 3)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 3");
      }
      if (checkTwo(marker, 3, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("3 2");
      }
      if (checkTwo(marker, 0, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 2");
      }
      if (checkTwo(marker, 4, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[3].toString()  + "'");
        checker.add("4 6");
      }
      if (checkTwo(marker, 4, 5)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("4 5");
      }
      if (checkTwo(marker, 5, 7)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[3].toString());
        checker.add("5 7");
      }
      if (checkTwo(marker, 7, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("7 6");
      }
      if (checkTwo(marker, 12, 13)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("12 13");
      }
      if (checkTwo(marker, 13, 15)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[3].toString());
        checker.add("13 15");
      }
      if (checkTwo(marker, 15, 14)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("15 14");
      }
      if (checkTwo(marker, 12, 14)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[3].toString() + "'");
        checker.add("12 14");
      }
      if (checkTwo(marker, 8, 9)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("8 9");
      }
      if (checkTwo(marker, 9, 11)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[3].toString());
        checker.add("9 11");
      }
      if (checkTwo(marker, 11, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("11 10");
      }
      if (checkTwo(marker, 8, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("8 10");
      }
      if (checkTwo(marker, 0, 4)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[0].toString()  + "'");
        checker.add("0 4");
      }
      if (checkTwo(marker, 4, 12)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[1].toString());
        checker.add("4 12");
      }
      if (checkTwo(marker, 0, 8)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("0 8");
      }
      if (checkTwo(marker, 12, 8)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[0].toString());
        checker.add("12 8");
      }
      if (checkTwo(marker, 1, 5)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[0].toString() + "'");
        checker.add("1 5");
      }
      if (checkTwo(marker, 5, 13)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[1].toString());
        checker.add("5 13");
      }
      if (checkTwo(marker, 13, 9)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[0].toString());
        checker.add("13 9");
      }
      if (checkTwo(marker, 1, 9)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[1].toString() + "'");
        checker.add("1 9");
      }
      if (checkTwo(marker, 3, 7)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[0].toString() + "'");
        checker.add("3 7");
      }
      if (checkTwo(marker, 7, 15)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[1].toString());
        checker.add("7 15");
      }
      if (checkTwo(marker, 15, 11)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[0].toString());
        checker.add("15 11");
      }
      if (checkTwo(marker, 3, 11)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[1].toString() + "'");
        checker.add("3 11");
      }
      if (checkTwo(marker, 2, 6)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[0].toString() + "'");
        checker.add("2 6");
      }
      if (checkTwo(marker, 6, 14)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[1].toString());
        checker.add("6 14");
      }
      if (checkTwo(marker, 14, 10)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[0].toString());
        checker.add("14 10");
      }
      if (checkTwo(marker, 2, 10)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("2 10");
      }






      if (checkTwoOverlap(marker, 0, 1)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("0 1");
      }
      if (checkTwoOverlap(marker, 1, 3)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[3].toString());
        checker.add("1 3");
      }
      if (checkTwoOverlap(marker, 3, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("3 2");
      }
      if (checkTwoOverlap(marker, 0, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("0 2");
      }
      if (checkTwoOverlap(marker, 4, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[3].toString()  + "'");
        checker.add("4 6");
      }
      if (checkTwoOverlap(marker, 4, 5)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("4 5");
      }
      if (checkTwoOverlap(marker, 5, 7)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[3].toString());
        checker.add("5 7");
      }
      if (checkTwoOverlap(marker, 7, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("7 6");
      }
      if (checkTwoOverlap(marker, 12, 13)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString() + "'");
        checker.add("12 13");
      }
      if (checkTwoOverlap(marker, 13, 15)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[3].toString());
        checker.add("13 15");
      }
      if (checkTwoOverlap(marker, 15, 14)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString());
        checker.add("15 14");
      }
      if (checkTwoOverlap(marker, 12, 14)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[3].toString() + "'");
        checker.add("12 14");
      }
      if (checkTwoOverlap(marker, 8, 9)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[2].toString() + "'");
        checker.add("8 9");
      }
      if (checkTwoOverlap(marker, 9, 11)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[3].toString());
        checker.add("9 11");
      }
      if (checkTwoOverlap(marker, 11, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[2].toString());
        checker.add("11 10");
      }
      if (checkTwoOverlap(marker, 8, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + "'" + uniqueArray[3].toString() + "'");
        checker.add("8 10");
      }
      if (checkTwoOverlap(marker, 0, 4)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[0].toString()  + "'");
        checker.add("0 4");
      }
      if (checkTwoOverlap(marker, 4, 12)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[1].toString());
        checker.add("4 12");
      }
      if (checkTwoOverlap(marker, 0, 8)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("0 8");
      }
      if (checkTwoOverlap(marker, 12, 8)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + "'" + uniqueArray[0].toString());
        checker.add("12 8");
      }
      if (checkTwoOverlap(marker, 1, 5)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[0].toString() + "'");
        checker.add("1 5");
      }
      if (checkTwoOverlap(marker, 5, 13)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[1].toString());
        checker.add("5 13");
      }
      if (checkTwoOverlap(marker, 13, 9)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[0].toString());
        checker.add("13 9");
      }
      if (checkTwoOverlap(marker, 1, 9)) {
        solved.add(uniqueArray[2].toString() + "'" + uniqueArray[3].toString() + uniqueArray[1].toString() + "'");
        checker.add("1 9");
      }
      if (checkTwoOverlap(marker, 3, 7)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[0].toString() + "'");
        checker.add("3 7");
      }
      if (checkTwoOverlap(marker, 7, 15)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[1].toString());
        checker.add("7 15");
      }
      if (checkTwoOverlap(marker, 15, 11)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[0].toString());
        checker.add("15 11");
      }
      if (checkTwoOverlap(marker, 3, 11)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + uniqueArray[1].toString() + "'");
        checker.add("3 11");
      }
      if (checkTwoOverlap(marker, 2, 6)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[0].toString() + "'");
        checker.add("2 6");
      }
      if (checkTwoOverlap(marker, 6, 14)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[1].toString());
        checker.add("6 14");
      }
      if (checkTwoOverlap(marker, 14, 10)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[0].toString());
        checker.add("14 10");
      }
      if (checkTwoOverlap(marker, 2, 10)) {
        solved.add(uniqueArray[2].toString() + uniqueArray[3].toString() + "'" + uniqueArray[1].toString() + "'");
        checker.add("2 10");
      }








      if (checkSingle(marker, 0)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString()+ "'" + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 1)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString()+ "'" + uniqueArray[3].toString());
      }
      if (checkSingle(marker, 2)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString() + uniqueArray[3].toString());
      }
      if (checkSingle(marker, 3)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString() + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 4)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString()+ uniqueArray[2].toString()+ "'" + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 5)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString()+ "'" + uniqueArray[3].toString());
      }
      if (checkSingle(marker, 6)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString() + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 7)) {
        solved.add(uniqueArray[0].toString() + "'" + uniqueArray[1].toString() + uniqueArray[2].toString()+ uniqueArray[3].toString());
      }
      if (checkSingle(marker, 8)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString()+ "'" + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 9)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString()+ "'" + uniqueArray[3].toString());
      }
      if (checkSingle(marker, 10)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString() + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 11)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString()+ "'" + uniqueArray[2].toString() + uniqueArray[3].toString());
      }
      if (checkSingle(marker, 12)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString()+ "'" + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 13)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString()+ "'" + uniqueArray[3].toString());
      }
      if (checkSingle(marker, 14)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString() + uniqueArray[3].toString()+ "'");
      }
      if (checkSingle(marker, 15)) {
        solved.add(uniqueArray[0].toString() + uniqueArray[1].toString() + uniqueArray[2].toString() + uniqueArray[3].toString());
      }


      boolean noInput = true;
      for (int element : marker) {
        if (element != 0) {
          noInput = false;
        }
      }
      if (noInput) {
        solved.add("0");
      }
    } else {
      //POS
      if (kmap[0] == 0 && kmap[1] == 0 && kmap[2] == 0 && kmap[3] == 0 && kmap[4] == 0 && kmap[5] == 0 &&
      kmap[6] == 0 && kmap[7] == 0 && kmap[8] == 0 && kmap[9] == 0 &&kmap[10] == 0 &&
      kmap[11] == 0 && kmap[12] == 0 && kmap[13] == 0 && kmap[14] == 0 && kmap[15] == 0) {
        solved.add("0");
        Arrays.fill(marker, 2);
      }
      if (checkEight(marker, 0, 1, 2, 3, 4, 5, 6, 7)) {
        solved.add("(" +uniqueArray[0].toString() + ")");
        checker.add("0 1 2 3 4 5 6 7");
      }
      if (checkEight(marker, 12, 13, 15, 14, 8, 9, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString() + "')");
        checker.add("12 13 15 14 8 9 11 10");
      }
      if (checkEight(marker, 0, 1, 4, 5, 12, 13, 8, 9)) {
        solved.add("(" + uniqueArray[2].toString() + ")");
        checker.add("0 1 4 5 12 13 8 9");
      }
      if (checkEight(marker, 1, 3, 5, 7, 13, 15, 9, 11)) {
        solved.add("(" + uniqueArray[3].toString() + "')");
        checker.add("1 3 5 7 13 15 9 11");
      }
      if (checkEight(marker, 3, 2, 7, 6, 15, 14, 11, 10)) {
        solved.add("(" + uniqueArray[2].toString() + "')");
        checker.add("3 2 7 6 15 14 11 10");
      }
      if (checkEight(marker, 0, 4, 12, 8, 2, 6, 14, 10)) {
        solved.add("(" + uniqueArray[3].toString() + ")");
        checker.add("0 4 12 8 2 6 14 10");
      }
      if (checkEight(marker, 4, 5, 7, 6, 12, 13, 15, 14)) {
        solved.add("(" + uniqueArray[1].toString() + "')");
        checker.add("4 5 7 6 12 13 15 14");
      }
      if (checkEight(marker, 0, 1, 3, 2, 8, 9, 11, 10)) {
        solved.add("(" + uniqueArray[1].toString() + ")");
        checker.add("0 1 3 2 8 9 11 10");
      }




      if (checkEightOverlap(marker, 0, 1, 2, 3, 4, 5, 6, 7)) {
        solved.add("(" +uniqueArray[0].toString() + ")");
        checker.add("0 1 2 3 4 5 6 7");
      }
      if (checkEightOverlap(marker, 12, 13, 15, 14, 8, 9, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString() + "')");
        checker.add("12 13 15 14 8 9 11 10");
      }
      if (checkEightOverlap(marker, 0, 1, 4, 5, 12, 13, 8, 9)) {
        solved.add("(" + uniqueArray[2].toString() + ")");
        checker.add("0 1 4 5 12 13 8 9");
      }
      if (checkEightOverlap(marker, 1, 3, 5, 7, 13, 15, 9, 11)) {
        solved.add("(" + uniqueArray[3].toString() + "')");
        checker.add("1 3 5 7 13 15 9 11");
      }
      if (checkEightOverlap(marker, 3, 2, 7, 6, 15, 14, 11, 10)) {
        solved.add("(" + uniqueArray[2].toString() + "')");
        checker.add("3 2 7 6 15 14 11 10");
      }
      if (checkEightOverlap(marker, 0, 4, 12, 8, 2, 6, 14, 10)) {
        solved.add("(" + uniqueArray[3].toString() + ")");
        checker.add("0 4 12 8 2 6 14 10");
      }
      if (checkEightOverlap(marker, 4, 5, 7, 6, 12, 13, 15, 14)) {
        solved.add("(" + uniqueArray[1].toString() + "')");
        checker.add("4 5 7 6 12 13 15 14");
      }
      if (checkEightOverlap(marker, 0, 1, 3, 2, 8, 9, 11, 10)) {
        solved.add("(" + uniqueArray[1].toString() + ")");
        checker.add("0 1 3 2 8 9 11 10");
      }







      if (checkFour(marker, 0, 1, 3, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("0 1 3 2");
      }
      if (checkFour(marker, 4, 5, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + "')");
        checker.add("4 5 7 6");
      }
      if (checkFour(marker, 12, 13, 15, 14)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "')");
        checker.add("12 13 15 14");
      }
      if (checkFour(marker, 8, 9, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + ")");
        checker.add("8 9 11 10");
      }
      if (checkFour(marker, 0, 2, 8, 10)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 2 8 10");
      }
      if (checkFour(marker, 0, 1, 8, 9)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 1 8 9");
      }
      if (checkFour(marker, 1, 3, 9, 11)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + "')");
        checker.add("1 3 9 11");
      }
      if (checkFour(marker, 3, 2, 11, 10)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("3 2 11 10");
      }
      if (checkFour(marker, 0, 4, 2, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 4 2 6");
      }
      if (checkFour(marker, 4, 6, 12, 14)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[3].toString() + ")");
        checker.add("4 6 12 14");
      }
      if (checkFour(marker, 12, 14, 8, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[3].toString() + ")");
        checker.add("12 14 8 10");
      }
      if (checkFour(marker, 0, 1, 4, 5)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 1 4 5");
      }
      if (checkFour(marker, 1, 3, 5, 7)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[3].toString()+ "')");
        checker.add("1 3 5 7");
      }
      if (checkFour(marker, 3, 2, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("3 2 7 6");
      }
      if (checkFour(marker, 4, 5, 12, 13)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + ")");
        checker.add("4 5 12 13");
      }
      if (checkFour(marker, 5, 7, 13, 15)) {
        solved.add("(" + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + "')");
        checker.add("5 7 13 15");
      }
      if (checkFour(marker, 7, 6, 15, 14)) {
        solved.add("(" + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("7 6 15 14");
      }
      if (checkFour(marker, 12, 13, 8, 9)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[2].toString() + ")");
        checker.add("12 13 8 9");
      }
      if (checkFour(marker, 13, 15, 9, 11)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[3].toString()+ "')");
        checker.add("13 15 9 11");
      }
      if (checkFour(marker, 15, 14, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("15 14 11 10");
      }
      if (checkFour(marker, 0, 4, 12, 8)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 4 12 8");
      }
      if (checkFour(marker, 1, 5, 13, 9)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + "')");
        checker.add("1 5 13 9");
      }
      if (checkFour(marker, 3, 7, 15, 11)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "')");
        checker.add("3 7 15 11");
      }
      if (checkFour(marker, 2, 6, 14, 10)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + ")");
        checker.add("2 6 14 10");
      }







      if (checkFourOverlap(marker, 0, 1, 3, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("0 1 3 2");
      }
      if (checkFourOverlap(marker, 4, 5, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + "')");
        checker.add("4 5 7 6");
      }
      if (checkFourOverlap(marker, 12, 13, 15, 14)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "')");
        checker.add("12 13 15 14");
      }
      if (checkFourOverlap(marker, 8, 9, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + ")");
        checker.add("8 9 11 10");
      }
      if (checkFourOverlap(marker, 0, 2, 8, 10)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 2 8 10");
      }
      if (checkFourOverlap(marker, 0, 1, 8, 9)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 1 8 9");
      }
      if (checkFourOverlap(marker, 1, 3, 9, 11)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + "')");
        checker.add("1 3 9 11");
      }
      if (checkFourOverlap(marker, 3, 2, 11, 10)) {
        solved.add("(" + uniqueArray[1].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("3 2 11 10");
      }
      if (checkFourOverlap(marker, 0, 4, 2, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 4 2 6");
      }
      if (checkFourOverlap(marker, 4, 6, 12, 14)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[3].toString() + ")");
        checker.add("4 6 12 14");
      }
      if (checkFourOverlap(marker, 12, 14, 8, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[3].toString() + ")");
        checker.add("12 14 8 10");
      }
      if (checkFourOverlap(marker, 0, 1, 4, 5)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 1 4 5");
      }
      if (checkFourOverlap(marker, 1, 3, 5, 7)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[3].toString()+ "')");
        checker.add("1 3 5 7");
      }
      if (checkFourOverlap(marker, 3, 2, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("3 2 7 6");
      }
      if (checkFourOverlap(marker, 4, 5, 12, 13)) {
        solved.add("(" + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString() + ")");
        checker.add("4 5 12 13");
      }
      if (checkFourOverlap(marker, 5, 7, 13, 15)) {
        solved.add("(" + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + "')");
        checker.add("5 7 13 15");
      }
      if (checkFourOverlap(marker, 7, 6, 15, 14)) {
        solved.add("(" + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("7 6 15 14");
      }
      if (checkFourOverlap(marker, 12, 13, 8, 9)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[2].toString() + ")");
        checker.add("12 13 8 9");
      }
      if (checkFourOverlap(marker, 13, 15, 9, 11)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[3].toString()+ "')");
        checker.add("13 15 9 11");
      }
      if (checkFourOverlap(marker, 15, 14, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("15 14 11 10");
      }
      if (checkFourOverlap(marker, 0, 4, 12, 8)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 4 12 8");
      }
      if (checkFourOverlap(marker, 1, 5, 13, 9)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + "')");
        checker.add("1 5 13 9");
      }
      if (checkFourOverlap(marker, 3, 7, 15, 11)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "')");
        checker.add("3 7 15 11");
      }
      if (checkFourOverlap(marker, 2, 6, 14, 10)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + ")");
        checker.add("2 6 14 10");
      }





      if (checkTwo(marker, 0, 1)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 1");
      }
      if (checkTwo(marker, 1, 3)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString()+ "')");
        checker.add("1 3");
      }
      if (checkTwo(marker, 3, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("3 2");
      }
      if (checkTwo(marker, 0, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 2");
      }
      if (checkTwo(marker, 4, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString()  + ")");
        checker.add("4 6");
      }
      if (checkTwo(marker, 4, 5)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString() + ")");
        checker.add("4 5");
      }
      if (checkTwo(marker, 5, 7)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + "')");
        checker.add("5 7");
      }
      if (checkTwo(marker, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("7 6");
      }
      if (checkTwo(marker, 12, 13)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString() + ")");
        checker.add("12 13");
      }
      if (checkTwo(marker, 13, 15)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + "')");
        checker.add("13 15");
      }
      if (checkTwo(marker, 15, 14)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("15 14");
      }
      if (checkTwo(marker, 12, 14)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + ")");
        checker.add("12 14");
      }
      if (checkTwo(marker, 8, 9)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("8 9");
      }
      if (checkTwo(marker, 9, 11)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString()+ "')");
        checker.add("9 11");
      }
      if (checkTwo(marker, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("11 10");
      }
      if (checkTwo(marker, 8, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("8 10");
      }
      if (checkTwo(marker, 0, 4)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString()  + ")");
        checker.add("0 4");
      }
      if (checkTwo(marker, 4, 12)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString()+ "')");
        checker.add("4 12");
      }
      if (checkTwo(marker, 0, 8)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("0 8");
      }
      if (checkTwo(marker, 12, 8)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString()+ "')");
        checker.add("8 12");
      }
      if (checkTwo(marker, 1, 5)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString() + ")");
        checker.add("1 5");
      }
      if (checkTwo(marker, 5, 13)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + "' + " + uniqueArray[1].toString()+ "')");
        checker.add("5 13");
      }
      if (checkTwo(marker, 13, 9)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString()+ "')");
        checker.add("13 9");
      }
      if (checkTwo(marker, 1, 9)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString()+ "' + " + uniqueArray[1].toString() + ")");
        checker.add("1 9");
      }
      if (checkTwo(marker, 3, 7)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString() + ")");
        checker.add("3 7");
      }
      if (checkTwo(marker, 7, 15)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[1].toString()+ "')");
        checker.add("7 15");
      }
      if (checkTwo(marker, 15, 11)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString()+ "')");
        checker.add("15 11");
      }
      if (checkTwo(marker, 3, 11)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[1].toString() + ")");
        checker.add("3 11");
      }
      if (checkTwo(marker, 2, 6)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString() + ")");
        checker.add("2 6");
      }
      if (checkTwo(marker, 6, 14)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("6 14");
      }
      if (checkTwo(marker, 14, 10)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString() + ")");
        checker.add("14 10");
      }
      if (checkTwo(marker, 2, 10)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("2 10");
      }







      if (checkTwoOverlap(marker, 0, 1)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("0 1");
      }
      if (checkTwoOverlap(marker, 1, 3)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString()+ "')");
        checker.add("1 3");
      }
      if (checkTwoOverlap(marker, 3, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("3 2");
      }
      if (checkTwoOverlap(marker, 0, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("0 2");
      }
      if (checkTwoOverlap(marker, 4, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString()  + ")");
        checker.add("4 6");
      }
      if (checkTwoOverlap(marker, 4, 5)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString() + ")");
        checker.add("4 5");
      }
      if (checkTwoOverlap(marker, 5, 7)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + "')");
        checker.add("5 7");
      }
      if (checkTwoOverlap(marker, 7, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("7 6");
      }
      if (checkTwoOverlap(marker, 12, 13)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString() + ")");
        checker.add("12 13");
      }
      if (checkTwoOverlap(marker, 13, 15)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + "')");
        checker.add("13 15");
      }
      if (checkTwoOverlap(marker, 15, 14)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "')");
        checker.add("15 14");
      }
      if (checkTwoOverlap(marker, 12, 14)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[3].toString() + ")");
        checker.add("12 14");
      }
      if (checkTwoOverlap(marker, 8, 9)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString() + ")");
        checker.add("8 9");
      }
      if (checkTwoOverlap(marker, 9, 11)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString()+ "')");
        checker.add("9 11");
      }
      if (checkTwoOverlap(marker, 11, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[2].toString()+ "')");
        checker.add("11 10");
      }
      if (checkTwoOverlap(marker, 8, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + " + " + uniqueArray[3].toString() + ")");
        checker.add("8 10");
      }
      if (checkTwoOverlap(marker, 0, 4)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString()  + ")");
        checker.add("0 4");
      }
      if (checkTwoOverlap(marker, 4, 12)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString()+ "')");
        checker.add("4 12");
      }
      if (checkTwoOverlap(marker, 0, 8)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("0 8");
      }
      if (checkTwoOverlap(marker, 12, 8)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString()+ "')");
        checker.add("12 8");
      }
      if (checkTwoOverlap(marker, 1, 5)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString() + ")");
        checker.add("1 5");
      }
      if (checkTwoOverlap(marker, 5, 13)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString() + "' + " + uniqueArray[1].toString()+ "')");
        checker.add("5 13");
      }
      if (checkTwoOverlap(marker, 13, 9)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString()+ "')");
        checker.add("13 9");
      }
      if (checkTwoOverlap(marker, 1, 9)) {
        solved.add("(" + uniqueArray[2].toString() + " + " + uniqueArray[3].toString()+ "' + " + uniqueArray[1].toString() + ")");
        checker.add("1 9");
      }
      if (checkTwoOverlap(marker, 3, 7)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString() + ")");
        checker.add("3 7");
      }
      if (checkTwoOverlap(marker, 7, 15)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[1].toString()+ "')");
        checker.add("7 15");
      }
      if (checkTwoOverlap(marker, 15, 11)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[0].toString()+ "')");
        checker.add("15 11");
      }
      if (checkTwoOverlap(marker, 3, 11)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "' + " + uniqueArray[1].toString() + ")");
        checker.add("3 11");
      }
      if (checkTwoOverlap(marker, 2, 6)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString() + ")");
        checker.add("2 6");
      }
      if (checkTwoOverlap(marker, 6, 14)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("6 14");
      }
      if (checkTwoOverlap(marker, 14, 10)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[0].toString() + ")");
        checker.add("14 10");
      }
      if (checkTwoOverlap(marker, 2, 10)) {
        solved.add("(" + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + " + " + uniqueArray[1].toString() + ")");
        checker.add("2 10");
      }





      if (checkSingle(marker, 0)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ " + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 1)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ " + " + uniqueArray[3].toString() + "')");
      }
      if (checkSingle(marker, 2)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString() + "')");
      }
      if (checkSingle(marker, 3)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 4)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + "+ uniqueArray[2].toString()+ " + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 5)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ " + " + uniqueArray[3].toString() + ")");
      }
      if (checkSingle(marker, 6)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 7)) {
        solved.add("(" + uniqueArray[0].toString() + " + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "' + "+ uniqueArray[3].toString()+ "')");
      }
      if (checkSingle(marker, 8)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ " + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 9)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ " + " + uniqueArray[3].toString()+ "')");
      }
      if (checkSingle(marker, 10)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 11)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ " + " + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "')");
      }
      if (checkSingle(marker, 12)) {
        solved.add("("+ uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ " + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 13)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ " + " + uniqueArray[3].toString() + ")");
      }
      if (checkSingle(marker, 14)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString() + "' + " + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ ")");
      }
      if (checkSingle(marker, 15)) {
        solved.add("(" + uniqueArray[0].toString()+ "' + " + uniqueArray[1].toString()+ "' + " + uniqueArray[2].toString()+ "' + " + uniqueArray[3].toString()+ "')");
      }
      boolean noInput = true;
      for (int element : marker) {
        if (element != 0) {
          noInput = false;
        }
      }
      if (noInput) {
        solved.add("1");
      }
    }
    checkRedundancies(solved, checker, redundancyMarker);
  }
  public boolean checkEight(int[] marker, int term1, int term2, int term3, int term4, int term5, int term6, int term7, int term8) {
    if (sopOrPos) {
      if (kmap[term1] == 1 && kmap[term2] == 1 && kmap[term3] == 1 && kmap[term4] == 1 && kmap[term5] == 1 && kmap[term6] == 1 &&
      kmap[term7] == 1 && kmap[term8] == 1 && (marker[term1] == 0 && marker[term2] == 0 && marker[term3] == 0 &&
      marker[term4] == 0 && marker[term5] == 0 && marker[term6] == 0 && kmap[term7] == 0 && kmap[term8] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        marker[term5] = 2;
        marker[term6] = 2;
        marker[term7] = 2;
        marker[term8] = 2;
        return true;
      } else {
        return false;
      }
    } else {
      if (kmap[term1] == 0 && kmap[term2] == 0 && kmap[term3] == 0 && kmap[term4] == 0 && kmap[term5] == 0 && kmap[term6] == 0 &&
      kmap[term7] == 0 && kmap[term8] == 0 && (marker[term1] == 0 && marker[term2] == 0 && marker[term3] == 0 &&
      marker[term4] == 0 && marker[term5] == 0 && marker[term6] == 0 && kmap[term7] == 0 && kmap[term8] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        marker[term5] = 2;
        marker[term6] = 2;
        marker[term7] = 2;
        marker[term8] = 2;
        return true;
      } else {
        return false;
      }
    }
  }
  public boolean checkEightOverlap(int[] marker, int term1, int term2, int term3, int term4, int term5, int term6, int term7, int term8) {
    if (sopOrPos) {
      if (kmap[term1] == 1 && kmap[term2] == 1 && kmap[term3] == 1 && kmap[term4] == 1 && kmap[term5] == 1 && kmap[term6] == 1 &&
      kmap[term7] == 1 && kmap[term8] == 1 && (marker[term1] == 0 || marker[term2] == 0 || marker[term3] == 0 ||
      marker[term4] == 0 || marker[term5] == 0 || marker[term6] == 0 || kmap[term7] == 0 || kmap[term8] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        marker[term5] = 2;
        marker[term6] = 2;
        marker[term7] = 2;
        marker[term8] = 2;
        return true;
      } else {
        return false;
      }
    } else {
      if (kmap[term1] == 0 && kmap[term2] == 0 && kmap[term3] == 0 && kmap[term4] == 0 && kmap[term5] == 0 && kmap[term6] == 0 &&
      kmap[term7] == 0 && kmap[term8] == 0 && (marker[term1] == 0 || marker[term2] == 0 || marker[term3] == 0 ||
      marker[term4] == 0 || marker[term5] == 0 || marker[term6] == 0 || kmap[term7] == 0 || kmap[term8] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        marker[term5] = 2;
        marker[term6] = 2;
        marker[term7] = 2;
        marker[term8] = 2;
        return true;
      } else {
        return false;
      }
    }
  }
  public boolean checkFour(int[] marker, int term1, int term2, int term3, int term4) {
    if (sopOrPos) {
      if (kmap[term1] == 1 && kmap[term2] == 1 && kmap[term3] == 1 && kmap[term4] == 1 &&
      (marker[term1] == 0 && marker[term2] == 0 && marker[term3] == 0 && marker[term4] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        return true;
      } else {
        return false;
      }
    } else {
      if (kmap[term1] == 0 && kmap[term2] == 0 && kmap[term3] == 0 && kmap[term4] == 0 &&
      (marker[term1] == 0 && marker[term2] == 0 && marker[term3] == 0 && marker[term4] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        return true;
      } else {
        return false;
      }
    }

  }
  public boolean checkFourOverlap(int[] marker, int term1, int term2, int term3, int term4) {
    if (sopOrPos) {
      if (kmap[term1] == 1 && kmap[term2] == 1 && kmap[term3] == 1 && kmap[term4] == 1 &&
      (marker[term1] == 0 || marker[term2] == 0 || marker[term3] == 0 || marker[term4] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        return true;
      } else {
        return false;
      }
    } else {
      if (kmap[term1] == 0 && kmap[term2] == 0 && kmap[term3] == 0 && kmap[term4] == 0 &&
      (marker[term1] == 0 || marker[term2] == 0 || marker[term3] == 0 || marker[term4] == 0)) {

        marker[term1] = 2;
        marker[term2] = 2;
        marker[term3] = 2;
        marker[term4] = 2;
        return true;
      } else {
        return false;
      }
    }
  }
  public boolean checkTwo(int[] marker, int term1, int term2) {
    if (sopOrPos) {
      if (kmap[term1] == 1 && kmap[term2] == 1 && (marker[term1] == 0 && marker[term2] == 0)) {
        marker[term1] = 2;
        marker[term2] = 2;
        return true;
      } else {
        return false;
      }
    } else {
      if (kmap[term1] == 0 && kmap[term2] == 0 && (marker[term1] == 0 && marker[term2] == 0)) {
        marker[term1] = 2;
        marker[term2] = 2;
        return true;
      } else {
        return false;
      }
    }

  }
  public boolean checkTwoOverlap(int[] marker, int term1, int term2) {
    if (sopOrPos) {
      if (kmap[term1] == 1 && kmap[term2] == 1 && (marker[term1] == 0 || marker[term2] == 0)) {
        marker[term1] = 2;
        marker[term2] = 2;
        return true;
      } else {
        return false;
      }
    } else {
      if (kmap[term1] == 0 && kmap[term2] == 0 && (marker[term1] == 0 || marker[term2] == 0)) {
        marker[term1] = 2;
        marker[term2] = 2;
        return true;
      } else {
        return false;
      }
    }
  }
  public boolean checkSingle(int[] marker, int term1) {
    if (sopOrPos) {
      if (kmap[term1] == 1 && (marker[term1] == 0)) {
        marker[term1] = 2;
        return true;
      } else {
        return false;
      }
    } else {
      if (kmap[term1] == 0 && (marker[term1] == 0)) {
        marker[term1] = 2;
        return true;
      } else {
        return false;
      }
    }
  }
  public String solvedToString() {
    String solvedString = "";
    if (sopOrPos) {
      for (int i = 0; i < solved.size() - 1; i++) {
        solvedString += solved.get(i) + " + ";
      }
      solvedString += solved.get(solved.size() - 1);
    } else {
      for (int i = 0; i < solved.size() - 1; i++) {
        solvedString += solved.get(i);
      }
      solvedString += solved.get(solved.size() - 1);
    }
    return solvedString;
  }

}
