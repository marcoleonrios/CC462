import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class LoadImage {

    public static final int BLUR = 0;
    public static final int EDGE_DETECT = 1;
    public static final int EMBOSS = 2;
    public static final int BOX_BLUR = 3;

    public static BufferedImage getImagefromMatrix(int[][] matrix) {
        int width = matrix.length;
        int height =  matrix[0].length;
        
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, matrix[i][j]);
            }
        }
        return bufferedImage;
    }

    public static void saveImageFromMatrix(int[][] matrix, String filename) {
        int width = matrix.length;
        int height =  matrix[0].length;
        
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedImage.setRGB(i, j, matrix[i][j]);
            }
        }
        File outputfile = new File(filename);
        try {
            ImageIO.write(bufferedImage, "jpg", outputfile);
        }
        catch(Exception e) {
            System.err.println("ERROR AL GRABAR");
            System.err.println(e);
        }
    }

    public static int[][] getMatrixOfImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth(null);
        int height = bufferedImage.getHeight(null);
        int[][] pixels = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = bufferedImage.getRGB(i, j);
            }
        }
    
        return pixels;
    }

    public static int[][] getMatrixOfImage(String filename) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.err.println("Error al cargar el archivo");
            System.err.println(e);
            return null;
        }

        int width = bufferedImage.getWidth(null);
        int height = bufferedImage.getHeight(null);
        int[][] pixels = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = bufferedImage.getRGB(i, j);
            }
        }
    
        return pixels;
    }

    private static int paintItBlack(int argb, boolean _alpha_) {
        int annn, alpha, red, green, bleu;
        bleu = argb & 0xff;
        green = (argb & 0xff00)>>8;
        red = (argb & 0xff0000)>>16;
        if (_alpha_) alpha = argb & 0xff000000;
        else alpha = 0xff000000;
        int n = (bleu + green + red) / 3;
        
        annn = alpha + n + (n<<8) + (n<<16);

        return annn;
    }

    public static int[][] matrixToGrayScale(int[][] matrix, boolean _alpha_){
        int maxX = matrix.length;
        int maxY = matrix[0].length;
        int [][] pixels = new int[maxX][maxY];

        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                pixels[i][j] = paintItBlack(matrix[i][j], _alpha_);
            }
        }

        return pixels;
    }

    public static int[][] convolution(int [][] matrix, int kernel_) {
        int width = matrix.length;
        int height = matrix[0].length;
        int[][] pixels = new int[width][height];
        double[][] kernel;

        
        if (kernel_ == EDGE_DETECT) {
            kernel = new double[][] {
                {0.0, 1.0, 0.0},
                {1.0,-4.0, 1.0},
                {0.0, 1.0, 0.0}
            };
        }
        else if (kernel_ == EMBOSS) {
            kernel = new double [][] {
                {-2,-1, 0},
                {-1, 1, 1},
                {0, 1, 2}
            };
        }
        else if (kernel_ == BLUR) {
            kernel = new double[][] {
                {0.0625,0.125,0.0625},
                {0.125,0.25,0.125},
                {0.0625,0.125,0.0625}};
        }
        else if (kernel_ == BOX_BLUR) {
            kernel = new double[][] {
                {0.0, -1.0, 0.0},
                {-1.0, 5.0, -1.0},
                {0.0, -1.0, 0.0}
            };
        }
        
        else {
            kernel = new double[][] {
                {0.0625,0.125,0.0625},
                {0.125,0.25,0.125},
                {0.0625,0.125,0.0625}};
        }


        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y ++) {
                double r = 0f, g = 0f, b = 0f, a = 0f;
                for (int i = 0; i < kernel.length; i++) {
                    for (int j = 0; j < kernel.length; j++) {
                        int sX = (x - 1 + i + width) % width;
                        int sY = (y - 1 + j + height) % height;
                        int red, green, bleu;
                        int alpha = matrix[x][y] & 0xff000000;
                        
                        bleu = matrix[sX][sY] & 0xff;
                        green = (matrix[sX][sY] & 0xff00)>>8;
                        red =  (matrix[sX][sY] & 0xff0000)>>16;
                        r = r + red * kernel[i][j];
                        g = g + green * kernel[i][j];
                        b = b + bleu * kernel[i][j];
                        a = a + alpha;
                    }
                }
                a = (int)(a / 9);
                b = Math.min(Math.max(b, 0), 255);
                r = Math.min(Math.max(r, 0), 255);
                g = Math.min(Math.max(g, 0), 255);
                pixels[x][y] = (int)a + (int)b + ((int)g<<8) + ((int)r<<16);
            }
        }

        return pixels;
    }
}