import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class tweets_k_means {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		System.out.println("Processing Started...");
		String inputFile = args[2];
		String seedFile = args[1];
		String outputFile = args[3];
		Scanner sc = null;

		int clusterNos = 0;

		try {

			clusterNos = Integer.parseInt(args[0]);
		}
		catch (Exception e) {

		}

		if (clusterNos == 0)
			clusterNos = 25;
		try {
			sc = new Scanner(new File(inputFile),"UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//format id,text
		ArrayList<ArrayList<String>> data = new ArrayList<>();

		int cnt = 0;

		//JSON read and parsing for id,text

		while(sc.hasNextLine())
		{
			String str = sc.nextLine();
			str = str.toLowerCase();
			str = str.replace("\\\"", "");

			String tt = getJsonValue(str,"text").trim();
			String tt1 = getJsonValueId(str,"id").trim();

			ArrayList<String> tempList = new ArrayList<>();

			tempList.add(tt1.trim());
			tempList.add(tt);
			tempList.add(" ");

			data.add(tempList);
			cnt++;
		}

		sc.close();

		//		for (int i = 0; i < data.size(); i++) {
		//			System.out.println(data.get(i));
		//		}

		ArrayList<String> seed = new ArrayList<>();

		try {
			sc = new Scanner(new File(seedFile),"UTF-8");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		cnt = 0;
		while(sc.hasNextLine())
		{
			String str = sc.nextLine();
			cnt ++;
			if(cnt <= clusterNos)
			{
				str = str.replaceAll(",", "");	
				seed.add(str);
			}
		}
		sc.close();

		int change = 1;
		int steps = 100;
		while(change == 1 && steps > 0)
		{

			for (int i = 0; i < data.size(); i++) {
				ArrayList<Double> distList = new ArrayList<>();
				for (int j = 0; j < seed.size(); j++) {

					Double dist = getJaccardDist(data.get(i).get(1),gettwit(seed.get(j),data));

					distList.add(dist);
				}

				int clusterId = getCluster(distList);

				data.get(i).remove(2);
				data.get(i).add(2, seed.get(clusterId));
			}

			//update clusters

			change = updateClusters(data,seed);
			steps --;
		}

		FileWriter writer = new FileWriter(new File(outputFile));
		for (int i = 0; i < seed.size(); i++) {
			String op = (i+1)+ "  ";
			//System.out.print("Cluser id: " + (i+1) + "  twitID:" + seed.get(i) + " :");
			for (int j = 0; j < data.size(); j++) {
				if(data.get(j).get(2).equals(seed.get(i)))
					//System.out.print(", "+ data.get(j).get(0));
					op = op + data.get(j).get(0) + ",";

			}
			op = op.substring(0, op.length()-1);
			writer.write(op);
			writer.write("\n");
		}
		
		Double sse = getSSE(data,seed,clusterNos);
		writer.write("SSE :" + sse);
		writer.flush();
		writer.close();

		System.out.println("Processing completed...");
	}

	private static Double getSSE(ArrayList<ArrayList<String>> data, ArrayList<String> seed, int clusterNos) {
		// TODO Auto-generated method stub
		Double clusterDist = 0.0;
		for (int i = 0; i < clusterNos; i++) {
			for (int j = 0; j < data.size(); j++) {
				Double dist = 0.0;

				if(data.get(j).get(2).equals(seed.get(i)))
				{
					dist = getJaccardDist(data.get(j).get(1), gettwit(seed.get(i), data));
					dist = dist * dist;
					clusterDist = clusterDist + dist;
				}
			}
		}
		return clusterDist;
	}

	private static String getJsonValueId(String input, String keyName) {
		// TODO Auto-generated method stub

		char[] data = input.toCharArray();
		int startflag = 0;
		int startPos = 0;
		int keyFound = -1;
		String sub = "";

		for (int i = 0; i < input.length(); i++) {

			if(input.charAt(i) == '"' || startflag==1)
			{
				if (input.charAt(i) == '"')
					startflag = startflag + 1;

				if(startflag == 1)
				{
					if(input.charAt(i) != '"')
						sub = sub + input.charAt(i);
				}

				if(startflag == 2)
				{
					if(sub.equalsIgnoreCase(keyName))
					{
						keyFound = i;
						break;
					}
					else
					{
						sub = "";
						startflag = 0;	
					}
				}
			}
		}
		startflag = 0;
		startPos = 0;
		int valfound =-1;
		String val = "";
		if(keyFound!=-1)
			for (int i = keyFound + 3; i < data.length; i++) {

				if(input.charAt(i) != ',')
					val = val + input.charAt(i);
				if(input.charAt(i) == ',')
					return val;

			}
		return null;
	}

	private static String getJsonValue(String input, String keyName) {
		// TODO Auto-generated method stub

		char[] data = input.toCharArray();
		int startflag = 0;
		int startPos = 0;
		int keyFound = -1;
		String sub = "";

		for (int i = 0; i < input.length(); i++) {

			if(input.charAt(i) == '"' || startflag==1)
			{
				if (input.charAt(i) == '"')
					startflag = startflag + 1;

				if(startflag == 1)
				{
					if(input.charAt(i) != '"')
						sub = sub + input.charAt(i);
				}

				if(startflag == 2)
				{
					if(sub.equalsIgnoreCase(keyName))
					{
						keyFound = i;
						break;
					}
					else
					{
						sub = "";
						startflag = 0;	
					}
				}
			}
		}
		startflag = 0;
		startPos = 0;
		int valfound =-1;
		String val = "";
		if(keyFound!=-1)
			for (int i = keyFound + 1; i < data.length; i++) {

				if(input.charAt(i) == '"' || startflag==1)
				{
					if (input.charAt(i) == '"')
						startflag = startflag + 1;

					if(startflag == 1)
					{
						if(input.charAt(i) != '"')
							val = val + input.charAt(i);
					}

					if(startflag == 2)
					{
						return val;
					}
				}
			}
		return null;
	}

	private static String gettwit(String string, ArrayList<ArrayList<String>> data) {
		// TODO Auto-generated method stub

		for (int i = 0; i <data.size(); i++) {
			if(data.get(i).get(0).equalsIgnoreCase(string))
				return data.get(i).get(1);
		}
		return null;
	}

	private static int updateClusters(ArrayList<ArrayList<String>> data, ArrayList<String> seed) {
		// TODO Auto-generated method stub

		ArrayList<String> newSeed = new ArrayList<>();
		
		int change = 1;
		for (int i = 0; i < seed.size(); i++) {

			ArrayList<ArrayList<String>> data1 = new ArrayList<>();

			for (int j = 0; j < data.size(); j++) {

				if(data.get(j).get(2).equalsIgnoreCase(seed.get(i)))
					data1.add(data.get(j));
			}

			newSeed.add(i, getnewSeed(data1));
		}

		int iCnt = 0;
		
		for (int j = 0; j < seed.size(); j++) {
			if(seed.get(j).equalsIgnoreCase(newSeed.get(j)))
				iCnt++;
		}
		
		if(iCnt == seed.size())
			change = 0;
		seed = new ArrayList<>();
		seed = newSeed;
		return change;
	}

	private static String getnewSeed(ArrayList<ArrayList<String>> data1) {
		// TODO Auto-generated method stub
		ArrayList<Double> dist = new ArrayList<>();

		for (int i = 0; i < data1.size(); i++) {
			Double distance  = 0.0;
			for (int j = 0; j < data1.size(); j++) {
				distance = distance + getJaccardDist(data1.get(i).get(0), data1.get(j).get(0));	
			}
			dist.add(distance);
		}

		int loc = getCluster(dist);
		return data1.get(loc).get(1);
	}

	private static int getCluster(ArrayList<Double> distList) {
		// TODO Auto-generated method stub
		Double min = distList.get(0);
		int min_loc = 0;
		for (int i = 1; i < distList.size(); i++) {
			if(distList.get(i) < min)
			{
				min = distList.get(i);
				min_loc = i;
			}
		}
		return min_loc;
	}

	public static double getJaccardDist(String a,String b)
	{
		String[] aa = a.split(" ");
		String[] bb = b.split(" ");

		int common = 0;

		int common1 = 0;
		for (int i = 0; i < aa.length; i++) {
			int cFlag = 1;
			for (int j = 0; j < bb.length; j++) {

				if(aa[i].equalsIgnoreCase(bb[j]))
				{
					if(cFlag==1)
					{
						common++;
						cFlag = 0;
					}

					common1++;	
				}

			}
		}

		ArrayList<String> dummy = new ArrayList<>();

		for (int i = 0; i < aa.length; i++) {
			if(!dummy.contains(aa[i]))
				dummy.add(aa[i]);
		}

		for (int i = 0; i < bb.length; i++) {
			if(!dummy.contains(bb[i]))
				dummy.add(bb[i]);
		}

		float union = dummy.size();
		float intersection = common;
		float answer = (union - intersection) / union;
		return answer;
	}

}
