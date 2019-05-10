import java.util.Scanner;


public class ImageApp {
    public static void main(String[] args) {
        int image[][][], flippedImage[][][], greyscaledImage[][][], rotatedImage[][][];

        System.out.println("Please enter the whole name of a .ppm file:");
        Scanner in = new Scanner(System.in);
        String file = in.nextLine();

        image = PPMInputOutput.readPPMFile(file);

        System.out.println("Please enter the number to execute the image manipulation:" +
                "\n" + "1. Vertical flip." +
                "\n" + "2. Greyscale." +
                "\n" + "3. 90 degree clockwise rotation.");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        int executionNumber = Integer.parseInt(input);

        switch (executionNumber) {
            case 1:
                flippedImage = verticalFlip(image);
                PPMInputOutput.writePPMFile("out_" + file, flippedImage);
                break;

            case 2:
                greyscaledImage = greyScale(image);
                PPMInputOutput.writePPMFile("out_" + file, greyscaledImage);
                break;

            case 3:
                rotatedImage = rotateClockise(image);
                PPMInputOutput.writePPMFile("out_" + file, rotatedImage);
                break;
            default:
                System.out.println("ERROR: only numbers from 1 to 3 can be put in");

        }


    }

    /**
     * This method vertically flips RGB image array
     * k is countercounter used in order to write forst row of real image into last row od flipped image and so on.
     *
     * @param imageArray
     * @return
     */
    public static int[][][] verticalFlip(int[][][] imageArray) {
        int rows, columns, k;

        rows = imageArray.length;
        columns = imageArray[0].length;
        k = rows - 1;
        int[][][] flippedArray = new int[rows][columns][3];

        for (int i = 0; i < rows; i++) {
            flippedArray[k] = imageArray[i];
            k--;
        }
        System.out.println("Your image has been vertically flipped.");
        return flippedArray;
    }

    /**
     * This method rotates RGB image array 90 degrees clockwise
     *
     * @param imageArray
     * @return
     */
    public static int[][][] rotateClockise(int[][][] imageArray) {
        int newRows, newColumns;
        newRows = imageArray[0].length;
        newColumns = imageArray.length;
        int[][][] rotatedImage = new int[newRows][newColumns][3];

        newColumns = newColumns - 1;
        for (int i = 0; i < imageArray.length; i++) {
            newRows = 0;
            for (int j = 0; j < imageArray[0].length; j++) {
                rotatedImage[newRows][newColumns] = imageArray[i][j];
                newRows++;
            }
            newColumns--;
        }
        System.out.println("Your image has been rotated 90 degrees clockwise.");

        return rotatedImage;
    }

    /**
     * This method converts image into grayscaled using average method
     *
     * @param imageArray
     * @return
     */
    public static int[][][] greyScale(int[][][] imageArray) {
        int rows, columns, sum, avg;
        int[][][] greyscaledImage = new int[imageArray.length][imageArray[0].length][3];

        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[0].length; j++) {
                sum = 0;
                for (int k = 0; k < 3; k++) {
                    sum += imageArray[i][j][k];
                }
                avg = sum / 3;
                for (int k = 0; k < 3; k++) {
                    greyscaledImage[i][j][k] = avg;
                }
            }
        }
        System.out.println("Your image has been grey scaled by average method.");
        return greyscaledImage;
    }
}
