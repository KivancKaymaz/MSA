import java.io.*;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author KIVANÇ KAYMAZ
 *
 */

public class GlobalAlignment {

    private String inputString1;
    private String inputString2;
    private String inputString[] = new String[1000];
    public static int row = 0;
    public static int rowCount = 0;
    private Node[][] solveMatrix;
    PrintWriter writer = null;
    public int gap ;   // gap score
    public int match ;   // match score
    public int mismatch ; // mismatch score
    private String centerString = null;
    private String storeForScore1 = null;
    private String storeForScore2 = null;
    private String storeForScore[][] = new String[1000][1000];
    private int storeScore [] = new int[1000];
    public static int setScore = 0;
    public static int bestScore = 0;
    public static int temp_bestscore = 0;
    public static int beststringno;
    public static int starScore [][] = new int[1000][1000];
    public static int starScorex [][] = new int[1000][1000];
    private char Star[][] = new char[1000][1000];
    public GlobalAlignment() {
        readFile("inp.txt");

        for(int k = 0; k < row; k++) {
            temp_bestscore = 0;
            for(int p = 0; p < row; p++) {
                if( k != p) {
                    try {
                        writer = new PrintWriter("out.txt", "UTF-8");
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    inputString1 = inputString[k];
                    inputString2 = inputString[p];

                    final int lenString1 = inputString1.length() + 1;
                    final int lenString2 = inputString2.length() + 1;
                    solveMatrix = new Node[lenString2][lenString1];

                    for (int i = 0; i < lenString2; i++) {
                        for (int j = 0; j < lenString1; j++) {
                            solveMatrix[i][j] = new Node();
                        }
                    }

                        needlemanWunsch(lenString1, lenString2); // Needleman - Wunsch algorithm
                        traceback(lenString2 - 1, lenString1 - 1); // find all possibility

                        storeForScore[rowCount][0] = storeForScore1;
                        storeForScore[rowCount][1] = storeForScore2;
                        storeScore[rowCount] = setScore;
                        temp_bestscore = temp_bestscore + setScore;
                        rowCount++;
                }

                else continue;
            }
            if(temp_bestscore > bestScore){
                bestScore = temp_bestscore;
                beststringno = k;
            }
        }
        starAlign();
    }


    private void needlemanWunsch(final int lenString1, final int lenString2) {

        // initialize matrix
        for (int i = 0; i < lenString2; i++) {
            solveMatrix[i][0].setValue(gap * i);
            solveMatrix[i][0].pushDirection('n');
        }
        for (int i = 0; i < lenString1; i++) {
            solveMatrix[0][i].setValue(gap * i);
            solveMatrix[0][i].pushDirection('w');
        }

        boolean matchControl = false;

        for (int i = 1; i < lenString2; i++) {
            for (int j = 1; j < lenString1; j++) {

                int north = solveMatrix[i - 1][j].getValue() + gap;
                int west = solveMatrix[i][j - 1].getValue() + gap;
                int northWest = solveMatrix[i - 1][j - 1].getValue();

                if (inputString1.charAt(j - 1) == inputString2.charAt(i - 1)) {

                    Node controlNode = solveMatrix[i - 1][j - 1];
                    matchControl = true;

                    northWest += match;


                } else {
                    northWest += mismatch;
                    matchControl = false;
                }

                final int max = Math.max(Math.max(northWest, west), north);

                solveMatrix[i][j].setValue(max);

                if (northWest == max) {
                    solveMatrix[i][j].pushDirection('c');
                    if (matchControl)
                        solveMatrix[i][j].setMatchWay(true); // for extension match score
                }
                if (west == max) {
                    solveMatrix[i][j].pushDirection('w');
                }
                if (north == max) {
                    solveMatrix[i][j].pushDirection('n');
                }
            }
        }


    }

    private void traceback(int i, int j) {

        final Stack<Point> stack = new Stack<Point>();
        stack.push(new Point(i, j, "", "")); // first push

        do {

            final StringBuilder lastString1 = new StringBuilder(), lastString2 = new StringBuilder();

            final Point p = stack.pop();
            i = p.getX();
            j = p.getY();

            lastString1.append(p.getStoreString1()); // get back last string
            lastString2.append(p.getStoreString2()); // ""

            do {

                final Node n = solveMatrix[i][j];
                char forSwitchChar;

                if (n.sizeDirection() > 1) {
                    stack.push(new Point(i, j, lastString1.toString(),
                            lastString2.toString()));
                    forSwitchChar = n.popDirection();
                } else {
                    forSwitchChar = n.peekDirection();
                }

                switch (forSwitchChar) {

                    case 'c':
                        // go northwest
                        lastString1.append(inputString1.charAt(--j));
                        lastString2.append(inputString2.charAt(--i));
                        break;
                    case 'w':
                        // go west
                        lastString1.append(inputString1.charAt(--j));
                        lastString2.append('-');
                        break;
                    case 'n':
                        // go north
                        lastString1.append('-');
                        lastString2.append(inputString2.charAt(--i));
                        break;
                }

            } while (i > 0 || j > 0);

            storeForScore1 = lastString1.reverse().toString();
            storeForScore2 = lastString2.reverse().toString();



        } while (stack.size() > 0);

    }

    private void readFile(final String location) {

        Scanner input = null;
        try {
            input = new Scanner(new File(location));
            String scoreMMG = input.nextLine();
            String scoreAr[] = scoreMMG.split(" ");

            match = Integer.parseInt(scoreAr[0]);
            mismatch = Integer.parseInt(scoreAr[1]);
            gap = Integer.parseInt(scoreAr[2]);

            String rowPlus = input.nextLine();
            while(rowPlus!=null) {
                inputString[row] = rowPlus;
                row++;
                if(input.hasNextLine())
                    rowPlus = input.nextLine();
                else
                    break;

            }

            for(int i= 0; i < row; i++)
            {
                for(int j = i; j < row; j++)
                {
                    if(i!=j) {
                        inputString1 = inputString[i];
                        inputString2 = inputString[j];

                    }
                    else
                        continue;
                }
            }

        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } finally {

            input.close();
        }

    }

    private void writeFile(char[][] str1, String cntr) {

        writer.println("KIVANC KAYMAZ");

        writer.println("Center String : " + cntr);
        writer.println();
        writer.println("Star Alignment");
        for(int i = 0; i < 1000; i++){
            for(int j = 0; j < 1000; j++) {
                if(str1[i][j] != 'X') {
                    writer.print(str1[i][j]);
                }
            }
            writer.println();
        }
        System.out.println("PLEASE CHECK 'out.txt' ");

    }


    private void starAlign(){

        char temporary[][] = new char[1000][1000];
        String tempSeq1 = null;
        String tempSeq2 = null;
        int columnsyc = 0;
        centerString = inputString[beststringno];

        for(int i=0;i<1000; i++)
            for(int j=0;j<1000;j++)
                temporary[i][j] = 'X';

        for(int i = 0; i < row; i++)
        {
            if(i != beststringno)
            {
                if(i <= beststringno) {
                    tempSeq1 = storeForScore[beststringno * (row - 1) + i][0];
                    tempSeq2 = storeForScore[beststringno * (row - 1) + i][1];
                }
                    else {
                    tempSeq1 = storeForScore[beststringno * (row - 1) + i - 1][0];
                    tempSeq2 = storeForScore[beststringno * (row - 1) + i - 1][1];
                }
                temporary[i] = tempSeq2.toCharArray();

                columnsyc = 0;
                for(int k = 0; k < tempSeq1.length() ; k++){
                    if(tempSeq1.charAt(k) == '-'){
                        starScore[i][columnsyc] = k + 1;
                        columnsyc ++;
                    }
                }

            }
            else{
                temporary[i] = centerString.toCharArray();
            }
        }

        int tmpSS = 0;
        int ifsame = 0;

        for (int x = 0; x<1000; x++){
            for (int y = 0; y<1000; y++){
                starScorex[x][y] = starScore[x][y];
            }
        }

            for(int j = 0; j < row; j++){
                for(int g = 0; g < 3; g++){
                    tmpSS = starScore[j][g];
                    if(tmpSS != 0) {
                        for (int r = j + 1; r < row; r++) {
                            for (int x = 0; x < 1000; x++) {
                                ifsame = 0;
                                for ( int a = 0; a <= g-1 ; a++) {
                                    if (starScorex[j][a] == starScorex[r][x]) {
                                        ifsame = 1;
                                    }
                                }
                                if (ifsame != 1){
                                    if (tmpSS < starScore[r][x] && starScore[r][x] != 0) {
                                        starScore[r][x]++;
                                    } else if (tmpSS > starScore[r][x] && starScore[r][x] != 0) {
                                        starScore[j][g]++;
                                    }
                                }

                            }
                        }
                    }
                    else
                        continue;
                }
            }


        for (int i=0; i<row; i++){
            for (int j=0; j<100; j++) {
                        Star[i][j] = 'X';
                    }
        }



    for (int i=0; i<row; i++){
        for (int j=0; j<row; j++) {
            if (i != j || beststringno == i) {    // itself not change (not center)
                for (int k = 0; k < 100; k++) {
                    if (starScore[j][k] != 0){
                    Star[i][starScore[j][k] - 1] = '-';}
                }
            }
        }
    }

        int max = 0;
        max = centerString.length();
        for (int i=0; i<1000;i++){
            for (int j=0;j<1000;j++) {
                if (starScore[i][j] != 0){
                    max ++;
                }
            }
        }
        int m = 0;


        for (int i=0;i<row;i++){
            m = 0;
                  for (int j=0;j<max;j++){
                      if(Star[i][j] != '-') {
                         Star[i][j] = temporary[i][m];
                          m++;
                      }
                  }
        }

        writeFile(Star,centerString);




    }
}