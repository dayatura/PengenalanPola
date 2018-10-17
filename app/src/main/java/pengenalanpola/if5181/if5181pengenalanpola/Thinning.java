package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Thinning {

    final static int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1},
            {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};

    final static int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6},
            {0, 4, 6}}};

    static List<Point> toWhite = new ArrayList<>();
    static int[][] grid;
    static int imgHeight;
    static int imgWidht;

    private static void initiateParams(Bitmap bitmap){
        imgHeight = bitmap.getHeight();
        imgWidht = bitmap.getWidth();
        grid = new int[imgWidht][imgHeight];

        for (int i = 0; i < imgWidht; i++) {
            for (int j = 0; j < imgHeight; j++) {
                int gs = ImageUtil.getPixelColor(bitmap, i, j)[3];
                if (gs > 180 ) grid[i][j] = 0;
                else grid[i][j] = 1;
            }
        }

    }



    public static Bitmap zShuen(Bitmap bitmap) {
        Bitmap skeleton = bitmap.copy(bitmap.getConfig(), true);

        initiateParams(bitmap);

        boolean firstStep = false;
        boolean hasChanged;

        do {
            hasChanged = false;
            firstStep = !firstStep;

            for (int r = 1; r < grid.length - 1; r++) {
                for (int c = 1; c < grid[0].length - 1; c++) {

                    if (grid[r][c] != 1)
                        continue;

                    int nn = numNeighbors(r, c);
                    if (nn < 2 || nn > 6)
                        continue;

                    if (numTransitions(r, c) != 1)
                        continue;

                    if (!atLeastOneIsWhite(r, c, firstStep ? 0 : 1))
                        continue;

                    toWhite.add(new Point(c, r));
                    hasChanged = true;
                }
            }

            for (Point p : toWhite)
                grid[p.y][p.x] = 0;
            toWhite.clear();

        } while (firstStep || hasChanged);

        for (int i = 0; i < imgWidht; i++) {
            for (int j = 0; j <imgHeight; j++) {
                if (grid[i][j] == 0 ) skeleton.setPixel(i, j, Color.WHITE);
                else skeleton.setPixel(i, j, Color.BLACK);
            }
        }

        return skeleton;
    }


    static int numNeighbors(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (grid[r + nbrs[i][1]][c + nbrs[i][0]] == 1)
                count++;
        return count;
    }

    static int numTransitions(int r, int c) {
        int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (grid[r + nbrs[i][1]][c + nbrs[i][0]] == 0) {
                if (grid[r + nbrs[i + 1][1]][c + nbrs[i + 1][0]] == 1)
                    count++;
            }
        return count;
    }

    static boolean atLeastOneIsWhite(int r, int c, int step) {
        int count = 0;
        int[][] group = nbrGroups[step];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < group[i].length; j++) {
                int[] nbr = nbrs[group[i][j]];
                if (grid[r + nbr[1]][c + nbr[0]] == 0) {
                    count++;
                    break;
                }
            }
        return count > 1;
    }


    public static Bitmap skeletonByMe(Bitmap bitmap) {
        int count;
        int[] border;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsb = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsb, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = floodFill(pixels, i, j, width);

                    skeleton(pixelsb, border[0], border[1], border[2], border[3], i, j, width);
                }
            }
        }

        return  Bitmap.createBitmap(pixelsb, width, height, bitmap.getConfig());
    }


    public static void skeleton(int[] pixels, int xmin, int ymin, int xmax, int ymax, int x, int y, int width) {
        int counterDirection, length, c, d, averageLength;

        int a = x;
        int b = y;
        int direction = 2;
        int totalLength = 0;
        int chainCount = 1;
        int[][] neighbours = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};

        do {
            int i = 0;
            for (; i < 8; i++) {
                int pixel = pixels[(a + neighbours[(direction + i + 5) % 8][0]) + (b + neighbours[(direction + i + 5) % 8][1]) * width] & 0x000000ff;
                if (pixel == 0) break;
            }

            if (i == 9) {
                return;
            } else {
                direction = (direction + i + 5) % 8;
                counterDirection = (direction + 2) % 8;
                c = a + neighbours[counterDirection][0];
                d = b + neighbours[counterDirection][1];
                length = 0;

                while ((pixels[c + d * width] & 0x000000ff) == 0) {
                    c = c + neighbours[counterDirection][0];
                    d = d + neighbours[counterDirection][1];
                    length += 1;
                }

                if (length > 1) {
                    totalLength += length;
                    chainCount++;
                }

                a = a + neighbours[direction][0];
                b = b + neighbours[direction][1];
            }
        }
        while (!(a == x && b == y));

        averageLength = totalLength / chainCount;
        a = x;
        b = y;
        direction = 2;

        do {
            int i = 0;
            for (; i < 8; i++) {
                int pixel = pixels[(a + neighbours[(direction + i + 5) % 8][0]) + (b + neighbours[(direction + i + 5) % 8][1]) * width] & 0x000000ff;
                if (pixel == 0) break;
            }

            if (i == 9) {
                return;
            } else {
                direction = (direction + i + 5) % 8;
                counterDirection = (direction + 2) % 8;
                c = a + neighbours[counterDirection][0];
                d = b + neighbours[counterDirection][1];
                length = 0;

                while ((pixels[c + d * width] & 0x000000ff) == 0) {
                    c = c + neighbours[counterDirection][0];
                    d = d + neighbours[counterDirection][1];
                    length++;
                }

                if (length > 1 && length <= averageLength) {
                    c = (c - 1 + a) / 2;
                    d = (d - 1 + b) / 2;
                    pixels[c + d * width] = pixels[c + d * width] | 0x0000ff00;
                }

                a = a + neighbours[direction][0];
                b = b + neighbours[direction][1];
            }
        }
        while (!(a == x && b == y));

        for (b = ymin; b <= ymax; b++) {
            for (a = xmin; a <= xmax; a++) {
                if ((pixels[a + b * width] & 0x00ffffff) == 0x0000ff00) {
                    pixels[a + b * width] = pixels[a + b * width] & 0xff000000;
                } else if ((pixels[a + b * width] & 0x000000ff) == 0) {
                    pixels[a + b * width] = pixels[a + b * width] | 0x00ffffff;
                }
            }
        }
    }


    public static int[] floodFill(int[] pixels, int x, int y, int width) {

        int xmax = x;
        int xmin = x;
        int ymax = y;
        int ymin = y;
        Queue<Integer> queueX = new LinkedList<>();
        Queue<Integer> queueY = new LinkedList<>();

        queueX.offer(x);
        queueY.offer(y);

        while (!queueX.isEmpty()) {
            x = queueX.poll();
            y = queueY.poll();

            int pixel = pixels[x + y * width] & 0x000000ff;

            if (pixel != 255) {
                pixels[x + y * width] = pixels[x + y * width] | 0x00ffffff;

                if (x < xmin) xmin = x;
                if (x > xmax) xmax = x;
                if (y < ymin) ymin = y;
                if (y > ymax) ymax = y;

                queueX.offer(x);
                queueY.offer(y + 1);
                queueX.offer(x);
                queueY.offer(y - 1);
                queueX.offer(x + 1);
                queueY.offer(y);
                queueX.offer(x - 1);
                queueY.offer(y);
            }
        }

        return new int[]{xmin, ymin, xmax, ymax};
    }

    public static int zhangSuenStep(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {
        int count = 0;

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x000000ff) == 0) {
                    int[] neighbours = {
                            pixels[i + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + j * width] & 0x000000ff,
                            pixels[(i + 1) + (j + 1) * width] & 0x000000ff,
                            pixels[i + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + j * width] & 0x000000ff,
                            pixels[(i - 1) + (j - 1) * width] & 0x000000ff
                    };
                    int[] function = zhangSuenAB(neighbours);

                    if (function[1] < 2 || 6 < function[1]) continue;
                    if (function[0] != 1) continue;
                    if (neighbours[0] != 255 && neighbours[2] != 255 && neighbours[4] != 255)
                        continue;
                    if (neighbours[2] != 255 && neighbours[4] != 255 && neighbours[6] != 255)
                        continue;

                    pixels[i + j * width] = pixels[i + j * width] | 0x0000ff00;
                }
            }
        }

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x00ffffff) == 0x0000ff00) {
                    pixels[i + j * width] = pixels[i + j * width] | 0x00ffffff;
                    count++;
                }
            }
        }

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x000000ff) == 0) {
                    int[] neighbours = {
                            pixels[i + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + j * width] & 0x000000ff,
                            pixels[(i + 1) + (j + 1) * width] & 0x000000ff,
                            pixels[i + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + j * width] & 0x000000ff,
                            pixels[(i - 1) + (j - 1) * width] & 0x000000ff
                    };
                    int[] function = zhangSuenAB(neighbours);

                    if (function[1] < 2 || 6 < function[1]) continue;
                    if (function[0] != 1) continue;
                    if (neighbours[0] != 255 && neighbours[2] != 255 && neighbours[6] != 255)
                        continue;
                    if (neighbours[0] != 255 && neighbours[4] != 255 && neighbours[6] != 255)
                        continue;

                    pixels[i + j * width] = pixels[i + j * width] | 0x0000ff00;
                }
            }
        }

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x00ffffff) == 0x0000ff00) {
                    pixels[i + j * width] = pixels[i + j * width] | 0x00ffffff;
                    count++;
                }
            }
        }

        return count;
    }

    private static int[] zhangSuenAB(int[] neighbours) {
        int countA = 0;
        int countB = 0;

        for (int i = 0; i < 8; i++) {
            if (neighbours[i] == 255 && neighbours[(i + 1) % 8] == 0) {
                countA++;
            }
            if (neighbours[i] == 0) {
                countB++;
            }
        }

        return new int[]{countA, countB};
    }


}
