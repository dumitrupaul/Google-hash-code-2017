/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.*;


public class Endpoint {
  double score = 0;
  int latencyToDatacenter;
  int cachesConnected;
  int[][] caches;
  public Map<Integer, Integer> videos_requested = new HashMap<Integer, Integer>();
  
  Endpoint(int latency, int nr_cache){
      latencyToDatacenter = latency;
      cachesConnected = nr_cache;
      caches = new int[nr_cache][2];
  }
  
  public void calculateScore(){
      if(cachesConnected == 0)
          score = 0;
      else{
          
          for(Map.Entry<Integer, Integer> entry : videos_requested.entrySet()){
              Integer key = entry.getKey();
              Integer value = entry.getValue();
              score += value/Main.videos.get(key);
          }
          
          score = score / cachesConnected * latencyToDatacenter; //de testat fara fractie si de tinut cont latencyToDB
      }
  }
    
}
