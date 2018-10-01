package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

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


    public static Bitmap skeleton(Bitmap bitmap) {
        Bitmap skeleton = bitmap.copy(bitmap.getConfig(), true);



        //TODO: convert to bitmap

        return skeleton;
    }

}
