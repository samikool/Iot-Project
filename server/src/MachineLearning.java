import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * MachineLearning is class that has different methods that are used in machine learning algorithms. All the methods in this class are
 * static and can be used without creating a machine learning object.
 * @author Sam Morgan
 */
public class MachineLearning {
    /**
     * cosineSimilarity is a method that will find the cosine similarities of two vectors rounded to the nearest 10,000th.
     * It is calculated with the formula shown <a href="https://en.wikipedia.org/wiki/Cosine_similarity"> here</a>.
     * The two vectors must be the same length.
     * {@link #dotProduct(double[], double[])} and {@link #magnitude(double[])} are used as helper methods to make the calculation look cleaner.
     * @param arrayOne array representing the first vector.
     * @param arrayTwo array representing the second vector.
     * @return the cosineSimilarity as a double of the two vectors.
     */
    public static double cosineSimilarity(double arrayOne[],double arrayTwo[]){
        double result=0;
        if(arrayOne.length == arrayTwo.length){
            result = dotProduct(arrayOne,arrayTwo) / (magnitude(arrayOne) * magnitude(arrayTwo));
        }

        //This math just rounds the result to the nearest 10,000
        result *= 10000;
        result = Math.round(result);
        result /= 10000;
        return result;
    }

    /**
     * euclideanDistance is a method that will find the euclidean distance between two points in Euclidean space rounded to the nearest 10,000th.
     * It is calculated with the formula shown <a href="https://en.wikipedia.org/wiki/Euclidean_distance"> here</a>.
     * The two data points can be in any dimension but they have to be the same dimension.
     * @param arrayOne arrayOne represents the first data point
     * @param arrayTwo arrayTwo represents the second data point
     * @return a double of the distance between the two data points.
     */
    public static double euclideanDistance(double arrayOne[], double arrayTwo[]){
        double result=0 ;
        if(arrayOne.length == arrayTwo.length){
            for(int i=0;i<arrayOne.length;i++){
                result += Math.pow(arrayOne[i] - arrayTwo[i],2);
            }
        }
        result = Math.sqrt(result);

        //This math just rounds result to the nearest 10,000
        result *= 10000;
        result = Math.round(result);
        result /= 10000;
        return result;
    }

    /**
     * hammingDistance is a method that takes two binary codes and finds the similarity between them.
     * It is calculated using the formula shown <a href="https://en.wikipedia.org/wiki/Hamming_distance"> here</a>.
     * The two codes need to be the same length. The lower the score the more similar the two codes are.
     * @param stringOne stringOne is the first binary code.
     * @param stringTwo stringTwo is the second binary code.
     * @return returns how many elements in each String are not equal to each other.
     */
    public static int hammingDistance(String stringOne, String stringTwo){
        int result=0;
        if(stringOne.length() == stringTwo.length()){
            for(int i=0;i<stringOne.length();i++){
                if(stringOne.charAt(i) != stringTwo.charAt(i)){
                    result++;
                }
            }
        }
        return result;
    }

    /**
     *knearest is a method that when provided training data, a data point, and k (the number of data points to consider in the training set) will find
     * what class the new data point should belong to using the KNearest neighbors algorithm.
     . The method will print the result to the console.
     * The training data must follow a specific file format exampled:
     * <a href="https://class-git.engineering.uiowa.edu/swd2018/sammorgan_swd/blob/master/oral_exam1/S27_MachineLearning_Hard/src/S27-MLMedium.csv"> here</a>.
     * More detail on the algorithm can be found <a href="https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm"> here</a>.
     * @param trainingData trainingData is the set of points to run the algorithm against.
     * @param dataPoint dataPoint is the data point that you want to classify. (must be same dimension as trainingData points.
     * @param k is the number number of neighbors to use to calculate the class the point belongs to
     */
    public static void knearest(File trainingData,double dataPoint[],int k){
        ArrayList<String[]> tempDataList = new ArrayList<String[]>(); //temporary list to hold data from file
        int numOfDataPoints = 0;//holds number of points
        int numOfDimensions = 0;//holds number of dimensions of data

        //this try block reads the file and stores the data in a temporary list
        try{
            String inputString = "";//temporary input string to hold lines of the file
            BufferedReader reader = new BufferedReader(new FileReader(trainingData.getPath()));

            //Loop to read the file, loops until it gets to the end of the file and there is no more to read
            while((inputString = reader.readLine()) != null){           //input String is assigned line of the file
                tempDataList.add(inputString.split(","));         //string is split into array of strings ie "1,2,class1" = String[]={"1","2","class1"}
                numOfDataPoints++;                                      //numOfDataPoints is set with this loop
            }
            numOfDimensions = tempDataList.get(0).length-1;
            reader.close();
        }catch (IOException e){
            System.out.println(e);
        }

        //Now that the program knows dimensions and number of points
        //the arrays to hold data can be initialized
        int[] fileClassData = new int[numOfDataPoints];                         //array to hold what class each data point is in
        double[][] fileNumData = new double[numOfDataPoints][numOfDimensions];  //array hold the actual data points
        double[] distance = new double[numOfDataPoints];                        //array to hold distance calculations
        int[] distanceIndex = new int[k];                                       //array to hold indexes of distances closest to passed in data point


        //Loop gathers data from the tempDataList
        //I technically could do tempDataList, but I think using arrays make things cleaner to read, and it looks messy to constantly
        //parse doubles stored as a string in the calculations
        for(int i=0; i<numOfDataPoints; i++){
            for(int j=0; j<numOfDimensions; j++){
                fileNumData[i][j] = Double.parseDouble(tempDataList.get(i)[j]);
            }
        }
        //Loops gathers class data from the tempDataList
        for(int i=0; i<numOfDataPoints; i++){
            if(tempDataList.get(i)[numOfDimensions].equalsIgnoreCase("\"class1\"")){
                fileClassData[i] = 1;
            }
            else if(tempDataList.get(i)[numOfDimensions].equalsIgnoreCase("\"class2\"")){
                fileClassData[i] = 2;
            }
        }

        //Distances are calculated using the euclideanDistance method
        //An array from the fileData is sent, along with the dataArray the method is interested in classifying
        //After this loop distance will hold the distances from every point of the training data to the passed in data point
        for(int i=0; i<fileNumData.length; i++){
            distance[i] = euclideanDistance(fileNumData[i],dataPoint);
        }

        //This loop finds the k lowest distances/numbers in the array
        //The indexes are then stored in distanceIndex so the program can later find what class they belong to
        for(int i=0; i<k; i++){
            int indexOfMin = 0;
            double min = distance[0];
            double max = distance[0];
            for(int j=0; j<numOfDataPoints; j++){
                if(distance[j] < min){
                    min = distance[j];
                    indexOfMin = j;
                }
                else if(distance[j] > max){
                    max = distance[j];
                }
            }
            distance[indexOfMin] = max;
            distanceIndex[i] = indexOfMin;
        }

        int class1Count=0;
        int class2Count=0;

        //This loop uses the distanceIndexes to find what the k nearest data point's classes are so it can assign the new point
        for(int i=0; i<k; i++){
            if(fileClassData[distanceIndex[i]] == 1){
                class1Count++;
            }
            else if(fileClassData[distanceIndex[i]] == 2){
                class2Count++;
            }
        }

        //Results are printed to the console.
        if(class1Count > class2Count){
            System.out.println("New data point belongs to class1");
        }
        else if(class1Count < class2Count){
            System.out.println("New data point belongs to class2");
        }
    }

    /**
     * kmeansCluster is a method that takes a data set of points in any dimension and a number of clusters K. It will then cluster the data
     * according to the kmeans algorithm shown <a href="https://en.wikipedia.org/wiki/K-means_clustering#Standard_algorithm"> here</a>.
     * The method will print the results to the console.
     * @param dataSet dataSet is the data that you want to cluster
     * @param k k is the number of clusters you want to end up with
     * @throws IllegalArgumentException if K is not greater than or equal to 2.
     */
    public static void kmeansCluster(File dataSet,int k){
        int numOfDataPoints = 0;//int will be used through the program to represent number of data points given in the file
        int numOfDimensions = 0;//int will be used throughout the program to represent number of dimensions the data is given in ie 1D,2D,3D
        ArrayList<String[]> tempDataList = new ArrayList<>();//ArrayList to temporarily store the numbers as a string before converting them to doubles

        //There method will only run if separating the data into at least two clusters
        if(k < 2){
            throw new IllegalArgumentException("K must be 2 or greater");
        }

        //the data is loaded in this try block
        //the file is read one line at a time
        try{
            String inputString = "";//Temporary String to hold a line read from the file
            BufferedReader reader = new BufferedReader(new FileReader(dataSet.getPath()));//point buffered reader to the file

            //This loop reads the file and loads the data from it
            //The loop runs as long as there are lines of data to read in the file
            //The program assumes data is given in a csv format containing one data point per line
            while((inputString = reader.readLine()) != null){       //input string assigned line of data
                tempDataList.add(inputString.split(","));     //data is split into a string array ie String "1,2,3" is split to StringArray[] = {"1","2","3"}
                numOfDataPoints++;                                  //numberOfDataPoints is set from this loop
            }
            numOfDimensions = tempDataList.get(0).length;           //number of dimensions is set
            reader.close();
        }

        //catch is file is not found
        catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        //catch if there is an input or output error
        catch (IOException e){
            System.out.println(e.getMessage());
        }

        //Now that number of data points, number of dimensions, and number of clusters,k, are all known we can initialize the Arrays for the algorithm
        double[][] dataArray = new double[numOfDataPoints][numOfDimensions];    //Array to hold the data
        double[][] centroid = new double[k][numOfDimensions];                   //Array to hold centroid values (means of clusters)
        double[][] previousCentroid = new double[k][numOfDimensions];           //Array to hold previous centroid values
        double[][] distanceOfCluster = new double[k][numOfDataPoints];          //Array to hold distances of data points to centroid
        int[] clusterAssignmentIndex = new int[numOfDataPoints];                //Array to hold the index of the cluster that the data points will be assigned to
        ArrayList<ArrayList<double[]>> cluster = new ArrayList<>();             //ArrayList to hold the clusters which are ArrayLists

        //this loop initializes dataArray so the program doesn't have to parseDoubles every time to get at the data
        for(int i=0; i<numOfDataPoints; i++){
            for(int j=0; j<numOfDimensions; j++){
                dataArray[i][j] = Double.parseDouble(tempDataList.get(i)[j]);
            }
        }

        //this loop initializes cluster with the correct amount of clusters(which are ArrayLists that hold a double[]), k=number of clusters
        for(int i=0; i<k; i++){
            cluster.add(new ArrayList<double[]>());
        }

        //initialize k numbers to start as means for centroids of clusters
        //initialized randomly by picking random indexes and using those as the starting centroids
        for(int i=0; i<k; i++){
            Random randomGenerator = new Random();
            int randomNum = randomGenerator.nextInt(numOfDataPoints);                   //number is stored so I can get the entire data point
            for(int j=0; j<numOfDimensions; j++){
                centroid[i][j] = dataArray[randomNum][j];
            }
        }

        //This loop will cluster data and only stop once convergence is reached
        //that happens when centroid and previousCentroid are equal
        while(!(arrayEquals(centroid,previousCentroid))){

            //This loops computes the distances, using the euclideanDistance method, of every data point to every centroid
            //i=centroidIndex and j=dataPointIndex
            //Distances are stored in the distanceOfCluster array distanceOfCluster[centroidIndex][dataPointIndex]
            //the distance of data point dataArray[0] to centroid[1] is stored in distanceOfCluster[1][0] and so on
            for(int i=0; i<k; i++){
                for(int j=0; j<numOfDataPoints; j++){
                    distanceOfCluster[i][j] = euclideanDistance(dataArray[j],centroid[i]);
                }
            }

            //This loops finds which cluster that dataArray[i] should be assigned to by comparing its distances to all k clusters
            //i=dataPointIndex and j=clusterIndex
            //for example, it saves the index(cluster number) of the minimum to clusterAssignmentIndex
            //so later data point at dataArray[i] should be assigned to cluster[clusterAssignmentIndex[i]]
            for(int i=0; i<numOfDataPoints; i++){
                int indexOfMin = 0;
                double min = distanceOfCluster[0][i];
                for(int j=0; j<k; j++){
                    if(distanceOfCluster[j][i] < min){
                        min = distanceOfCluster[j][i];
                        indexOfMin = j;
                    }
                }
                clusterAssignmentIndex[i] = indexOfMin;
            }

            //must clear the ArrayList first since the number of data points will most likely vary
            //meaning there could be less data points than currently in the list
            for(int i=0; i<k; i++){
                cluster.get(i).clear();
            }

            //this loop assigns the data held in the dataArray to the cluster based on clusterAssignmentIndex
            //It will assign each data point to the cluster that has a mean closest to the data point
            //this loop is why cluster is an ArrayList and not an array, since the amount of data
            //points that will be assigned to each cluster can vary from loop to loop

            for(int i=0; i<numOfDataPoints; i++){
                cluster.get(clusterAssignmentIndex[i]).add(dataArray[i]);
            }

            //This loop copies the current centroid's elements to the array previousCentroid so it can be compared the newly calculated centroid.
            //At the end of the loop, if centroid == previousCentroid then there is convergence and the loop terminates.
            //Since centroid and previousCentroid share the same structure, previous centroid's values are just overwritten
            for(int i=0; i<k; i++){
                for(int j=0; j<numOfDimensions; j++){
                    previousCentroid[i][j] = centroid[i][j];
                }
            }

            // this loop finds the means of the clusters by adding by going through each of the k clusters and computing the average
            //It follows the standard formula for finding the mean for multi-dimensional points:
            //Adding cluster[i]'s 1D elements then dividing by cluster[i]'s total number of elements
            //then repeating this process for the total number of dimensions the data has
            //i = number of the cluster, k, j = the dimension, and c = the data point in cluster[i]
            for(int i=0; i<k; i++){
                double total;
                if(cluster.get(i).size() > 0){
                    for(int j=0; j<numOfDimensions; j++){
                        total = 0;
                        for(int c=0; c<cluster.get(i).size(); c++){      // num of points in cluster
                            total += cluster.get(i).get(c)[j];           //cluster i, data point c, dimension j
                        }
                        total /= cluster.get(i).size();
                        centroid[i][j] = total;                          //assign new mean to centroid
                    }
                }
            }
        }



        //print out final cluster sizes after convergence
        for(int i=0; i<k; i++){
            System.out.println("Cluster " + i + ": " + cluster.get(i).size());
        }
        System.out.print("\n");
    }

    /**
     * dotProduct is a method that will find the dot product of two vectors. This means multiplying the same elements in
     * both vectors and then adding them together so {1,2}*{5,6} == 1*5 + 2*6 == 17.
     * It follows the formula given at <a href="https://en.wikipedia.org/wiki/Dot_product"> here</a>.
     * @param vectorOne the first vector. Its dimension must = vectorTwo's dimension.
     * @param vectorTwo the second vector. Its dimension must = vectorOne's dimension.
     * @return double whose value is the dot product of the two vectors
     */
    private static double dotProduct(double vectorOne[], double vectorTwo[]){
        double result=0;
        for(int i=0;i<vectorOne.length;i++){
            result += vectorOne[i] * vectorTwo[i];
        }
        return result;
    }

    /**
     * magnitude is a method which will return the magnitude of a vector. It is found by adding the square of every element together and then
     * taking the square root of that value.
     * @param vector vector to find the magnitude of.
     * @return double whose value is the magnitude of the vector.
     */
    private static double magnitude(double vector[]){
        double result=0;
        for(int i=0;i<vector.length;i++){
            result += Math.pow(vector[i],2);
        }
        result = Math.sqrt(result);
        return result;
    }

    /**
     * arrayEquals is an method that checks if two 2d arrays have the same elements. This method is used in the {@link #kmeansCluster(File, int)}
     * to figure out if the currently calculated centroid and the previous centroid are equal to each other.
     * function
     * @param arrayOne the first 2d array to compare.
     * @param arrayTwo the second 2d array to compare.
     * @return boolean that is true if every if every element in both arrays are equal.
     */
    private static boolean arrayEquals (double[][] arrayOne, double[][] arrayTwo){
        if(arrayOne.length != arrayTwo.length){
            return false;
        }

        for(int i=0; i<arrayOne.length; i++){
            if(arrayOne[i].length != arrayTwo[i].length){
                return false;
            }
            for (int j=0; j<arrayOne[i].length; j++){
                if (arrayOne[i][j] != arrayTwo[i][j]){
                    return false;
                }
            }
        }
        return true;
    }
}