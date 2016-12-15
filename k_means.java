import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class k_means {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		Scanner sc = null;

//		String inputFile = "C:/Users/nileshpharate/Box Sync/UTD-MS-CS/ML/Assignments/Assignment 5/test_data.txt";
//		String outputFile = "C:/Users/nileshpharate/Box Sync/UTD-MS-CS/ML/Assignments/Assignment 5/output.txt";
//		int clusterNos = 80;

		String inputFile = args[1];
		String outputFile = args[2];
		int clusterNos = Integer.parseInt(args[0]);
		try {
			sc = new Scanner(new File(inputFile),"UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<ArrayList<Double>> data = new ArrayList<>();

		int cnt = 0;

		//skip labels
		sc.nextLine();
		while(sc.hasNextLine())
		{
			cnt++;
			String str = sc.nextLine();

			String []new_data = str.split("	");

			ArrayList<Double> tempList = new ArrayList<>();

			tempList.add(Double.parseDouble(new_data[0]));
			tempList.add(Double.parseDouble(new_data[1]));
			tempList.add(Double.parseDouble(new_data[2]));
			tempList.add(0.0);
			data.add(tempList);
		}
		
		sc.close();

		//data - id,x,y

		ArrayList<ArrayList<Double>> centroid = new ArrayList<>();

		for (int i = 0; i < clusterNos; i++) {
			centroid.add(data.get(i));
		}

		int step = 25;
		while(step > 0)
		{
			for (int i = 0; i < data.size(); i++) {

				ArrayList<Double> distance = new ArrayList<>();

				for (int j = 0; j < centroid.size(); j++) {

					Double eDist = Math.sqrt(((centroid.get(j).get(1)-data.get(i).get(1)) * (centroid.get(j).get(1)-data.get(i).get(1)))
							+ ((centroid.get(j).get(2)-data.get(i).get(2)) * (centroid.get(j).get(2)-data.get(i).get(2))));
					distance.add(eDist);
				}

				int center = getMin(distance);
				ArrayList<Double> tList = data.get(i);
				tList.remove(3);
				tList.add(3, Double.parseDouble(""+center));
				//data.get(i).add(3, );		
			}

			for (int k = 0; k < centroid.size(); k++) {
				//System.out.println("i= " + i);
				int count = 0;
				Double x = 0.0;
				Double y = 0.0;
				for (int j = 0; j < data.size(); j++) {

					if(data.get(j).get(3) == k)
					{
						count++;

						x = x + data.get(j).get(1);
						y = y + data.get(j).get(2);
					}
				}

				centroid.get(k).set(1, x / count);
				centroid.get(k).set(2, y / count);
			}
			step--;
		}
          
		FileWriter writer = new FileWriter(new File(outputFile + "_" + clusterNos));
		
		for (int i = 0; i < clusterNos; i++) {
			String str = (i+1) + "    ";
			for (int j = 0; j < data.size(); j++) {

				if(data.get(j).get(3) == i)
				{
					str = str + (j+1) + ",";
				}
			}
			str = str.substring(0, str.length()-1);
			writer.write(str);
			writer.write('\n');
			//System.out.println(str);
		}

		Double sse= calcSSE(centroid,data,clusterNos);
		writer.write("SSE: " +sse);
		writer.flush();
		writer.close();
		//System.out.println("SSE: " + sse);
	}


	private static Double calcSSE(ArrayList<ArrayList<Double>> centroid, ArrayList<ArrayList<Double>> data, int clusterNos) {
		// TODO Auto-generated method stub
		Double clusterDist = 0.0;
		for (int i = 0; i < clusterNos; i++) {
			for (int j = 0; j < data.size(); j++) {
				Double dist = 0.0;

				if(data.get(j).get(3) == i)
				{
					dist = (centroid.get(i).get(1)-data.get(j).get(1)) * (centroid.get(i).get(1)-data.get(j).get(1));
					dist = dist * dist;

					clusterDist = clusterDist + dist;
				}
			}
		}

		return clusterDist;
	}


	private static int getMin(ArrayList<Double> distance) {
		// TODO Auto-generated method stub
		Double min = distance.get(0);
		int min_loc = 0;
		for (int i = 1; i < distance.size(); i++) {
			if(distance.get(i) < min)
			{
				min = distance.get(i);
				min_loc = i;
			}
		}
		return min_loc;
	}
}
