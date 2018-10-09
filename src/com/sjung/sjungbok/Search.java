package com.sjung.sjungbok;

import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

public class Search {

	//lägga till en metod som denna som matchar om de är 75% eller 80% matchning?
	public static int SimpleSearch(ArrayList<String> searchWords,ArrayList<String> textToSearch){
		int score=0;
		for(int i=0;i<searchWords.size();i++){
			if(textToSearch.contains(searchWords.get(i))){
				score++;
			}
			

		}
		return score;
	}

	public static int SimpleSearchFuzzy(ArrayList<String> searchWords,ArrayList<String> textToSearch){
		int score=0;
		
		String searchWord;
		
		
		for(int i=0;i<searchWords.size();i++){
			searchWord=searchWords.get(i).replace("Ø", "Ö").replace("ø", "ö");
			for(int j=0;j<textToSearch.size();j++){
				
				if(StringUtils.getJaroWinklerDistance(searchWord, textToSearch.get(j).replace("Ø", "Ö").replace("ø", "ö"))>=0.9){
					score++;
					break;
				}

			}
		}
		return score;
	}
}
