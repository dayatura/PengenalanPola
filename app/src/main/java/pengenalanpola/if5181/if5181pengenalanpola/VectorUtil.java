package pengenalanpola.if5181.if5181pengenalanpola;

public class VectorUtil {

    public static double similarity(double[] v1, double[] v2) {
        double s = 0;
        double s1 = 0;
        double s2 = 0;
        if (v1.length != v2.length)
            return 0;
        for (int i = 0; i < v1.length; i++) {
            s = s + v1[i] * v2[i];
            s1 = s1 + v1[i] * v1[i];
            s2 = s2 + v2[i] * v2[i];
        }
        return s / (Math.sqrt(s1 * s2));
    }


    public static int[][] rotateMatrix(int[][] filter) {
        return new int[][] {{filter[0][1],  filter[0][2],   filter[1][2]  },
                            {filter[0][0],  filter[1][1],   filter[2][2]  },
                            {filter[1][0],  filter[2][0],   filter[2][1]  }};
    }
}
