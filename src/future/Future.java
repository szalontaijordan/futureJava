package future;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Szalontai Jordán
 */
class Link {

    private final String title;
    private List<String> pointsTo;

    public Link(String title) {
        this.title = title;
        this.pointsTo = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void addPoint(String path) {
        pointsTo.add(path);
    }

    public void addAllPoints(List<String> sublist) {
        pointsTo.addAll(sublist);
    }

    public String getPoint(int i) {
        return pointsTo.get(i);
    }

    public List<String> points() {
        return pointsTo;
    }

    @Override
    public String toString() {
        return pointsTo.toString();
    }
}

/**
 * @author Szalontai Jordán
 */
public class Future {

    private static final String ROOT = "City";

    private double sum;
    private List<Link> graph;
    private List<Double> vector;
    private double[][] matrix;

    public Future(String rootPath) throws IOException {
        this.sum = 0;
        this.graph = new ArrayList<>();
        this.vector = new ArrayList<>();

        File root = new File(ROOT);
        readDirectories(root);

        ArrayList<String> titles = new ArrayList<>();

        for (Link l : graph) {
            titles.add(l.getTitle());
        }

        int N = titles.size();
        this.matrix = new double[N][N];

        
        // TITLE | LINK1, LINK2, ... , LINKn
        for (int i = 0; i < N; i++) {
            Link rowLink = graph.get(i);
            double rowSum = 0;

            for (String path : rowLink.points()) {
                int col = titles.indexOf(path);

                if (col >= 0) {
                    matrix[i][col] = 1;
                    rowSum++;
                }
            }

            if (rowSum != 0) {
                for (int j = 0; j < N; j++) {
                    matrix[i][j] /= rowSum;
                }
            }

            System.out.printf("%15s ", rowLink.getTitle().substring(1 + rowLink.getTitle().lastIndexOf("/")));
            // MATRIX
            for (int j = 0; j < N; j++) {
                System.out.printf("%.4f ", matrix[i][j]);
            }
            System.out.println();
        }

        // SUM
        System.out.println(sum);

        // VECTOR
        for (int i = 0; i < N; i++) {
            vector.set(i, vector.get(i)/* / sum*/);
        }
        for (int i = 0; i < N; i++) {
            System.out.println(vector.get(i) / sum);
        }

        // PAGERANK
        int iterations = 50;

        for (int it = 0; it < iterations; it++) {
            // itt van a vektor * matrix
            List<Double> newVector = new ArrayList<>();

            for (int i = 0; i < N; i++) {
                double newElement = 0;

                for (int j = 0; j < N; j++) {
                    newElement += matrix[i][j] * vector.get(j);
                }
                newVector.add(newElement);
            }

            vector = new ArrayList<>(newVector);

            sum = 0;
            for (int i = 0; i < N; i++) {
                sum += vector.get(i);
            }
            for (int i = 0; i < N; i++) {
                System.out.printf("%.10f\n", vector.get(i) / sum);
            }
            
            double egySzum = 0;
            for (int i = 0; i < N; i++) {
                egySzum += (vector.get(i) / sum);
            }
            
            System.out.println(egySzum);
            System.out.println();
        }

        System.out.println(iterations + " iteráció után.");
    }

    private void readDirectories(File dot) {
        File[] contents = dot.listFiles();

        for (File f : contents) {
            if (f.isFile()) {
                String filePath = f.getPath().replace("\\", "/");
                Link newLink = new Link(filePath);

                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    double people = Double.valueOf(br.readLine());

                    vector.add(people);
                    sum += people;

                    for (String line = br.readLine(); line != null; line = br.readLine()) {
                        newLink.addPoint(line);
                    }

                    br.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } finally {
                    graph.add(newLink);
                }
            } else {
                readDirectories(f);
            }
        }
    }

    public static void main(String[] args) {
        try {
            new Future(ROOT);
        } catch (IOException e) {
        }
    }
}
