
import java.io.*;
import java.util.*;

public class Main {
    
    private static Map<Integer, Integer> sortByValue(Map<Integer, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, Integer>> list =
                new LinkedList<Map.Entry<Integer, Integer>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/

        return sortedMap;
    }

    public static List<Integer> videos = new ArrayList<Integer>();
    public static List<Endpoint> endpoints = new ArrayList<Endpoint>();
    
    public static void main(String[] args) throws IOException {
        
        
        
        int nr_videos;
        int nr_endpoints;
        int nr_requests;
        int nr_caches;
        int cache_size;
        
        try{
            Scanner in = new Scanner(new BufferedReader(new FileReader("trending_today.in")));
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("trending_today.out")));
            
            //citire prima linie
            nr_videos = in.nextInt();
            nr_endpoints = in.nextInt();
            nr_requests = in.nextInt();
            nr_caches = in.nextInt();
            cache_size = in.nextInt();
            
            //initializare spatiu cache
            int caches_space[] = new int[nr_caches];
            for(int i = 0; i < nr_caches; i++)
                caches_space[i] = cache_size;
            
            //citire dimensiuni video
            for(int i = 0; i < nr_videos; i++){
                videos.add(in.nextInt());
                //System.out.println(videos.get(i));
            }
            
            //citire endpoints cu latenta si conexiuni la cache
            for(int i = 0; i < nr_endpoints; i++){
                int latency = in.nextInt();
                int aux = in.nextInt();
                //System.out.println("endpoint "+i+" latenta "+latency+" cache conected "+aux );
                endpoints.add(new Endpoint(latency, aux));
                for(int j = 0; j < aux; j++){
                    endpoints.get(i).caches[j][0] = in.nextInt();
                    endpoints.get(i).caches[j][1] = in.nextInt();
                   //System.out.println(endpoints.get(i).caches[j][0] +"   "+endpoints.get(i).caches[j][1]);
                }
                
                //sortare cache dupa latency
                for(int k = 0; k < endpoints.get(i).cachesConnected - 1; k++)
                    for(int l = k + 1; l < endpoints.get(i).cachesConnected; l++)
                        if(endpoints.get(i).caches[k][1] > endpoints.get(i).caches[l][1]){
                            int auxx = endpoints.get(i).caches[k][1];
                            endpoints.get(i).caches[k][1] = endpoints.get(i).caches[l][1];
                            endpoints.get(i).caches[l][1] = auxx;
                            
                            auxx = endpoints.get(i).caches[k][0];
                            endpoints.get(i).caches[k][0] = endpoints.get(i).caches[l][0];
                            endpoints.get(i).caches[l][0] = auxx;
                        }
     
            }
            
          
            
            //citire requests
            for(int i = 0; i < nr_requests; i++){
                int vid = in.nextInt();
                int end = in.nextInt();
                int req = in.nextInt();
                
                //de testat pentru video cu req/mb in loc de cel mai mare req
                int xd = req/videos.get(vid);
                endpoints.get(end).videos_requested.put(vid, xd);
                
            }
            
            //calcul scor pentru fiecare endpoint
            for(int i = 0; i < endpoints.size(); i++){
                endpoints.get(i).calculateScore();
                //System.out.println(endpoints.get(i).score);
            }
            
            //sortare endpoints dupa scor in arraylist descrescator
            for(int i = 0; i < endpoints.size() - 1; i++)
                for(int j = i + 1; j < endpoints.size(); j++)
                    if(endpoints.get(i).score < endpoints.get(j).score){
                        Endpoint aux = endpoints.get(i);
                        endpoints.set(i, endpoints.get(j));
                        endpoints.set(j, aux);
                    }

          
            //sortare video in endpoints dupa requests
            for(int i = 0; i < endpoints.size(); i++){
                Map<Integer, Integer> sortedMap = sortByValue(endpoints.get(i).videos_requested);
                endpoints.get(i).videos_requested = sortedMap;
                
                /*
                for(Map.Entry<Integer, Integer> entry : endpoints.get(i).videos_requested.entrySet()) {
                    Integer key = entry.getKey();
                    Integer value = entry.getValue();
                    System.out.println("key "+key+" value "+value +" endp " + i);
                }  */
            }
            
            ArrayList<Integer>[] videosInCache = new ArrayList[nr_caches];
            for(int i = 0; i < nr_caches; i++)
                videosInCache[i] = new ArrayList<Integer>();
            
            //adauga in cache
            for(int i = 0; i < endpoints.size(); i++){
                for(Map.Entry<Integer, Integer> entry : endpoints.get(i).videos_requested.entrySet()) {
                    Integer key = entry.getKey();
                    //System.out.println("" + key + " pe end " + i);
                    //Integer value = entry.getValue();
                    //System.out.println("key "+key+" value "+value +" endp " + i);
                    
                    if(videos.get(key) <= cache_size)
                    for(int j = 0; j < endpoints.get(i).cachesConnected; j++) {
                        if(videos.get(key) <= caches_space[endpoints.get(i).caches[j][0]]){
                            
                            
                            int s = 0;

                            for(int w = 0; w < videosInCache[endpoints.get(i).caches[j][0]].size(); w++)
                                
                                if( Objects.equals(videosInCache[endpoints.get(i).caches[j][0]].get(w), key)){
                                    
                                    s = 1;
                                    break;
                                }
                            
                            if(s == 0){
                                
                                videosInCache[endpoints.get(i).caches[j][0]].add(key);

                                
                                caches_space[endpoints.get(i).caches[j][0]] = caches_space[endpoints.get(i).caches[j][0]] -  videos.get(key);
                                break;
                            }
                        }
                    }
                    
                }    
            }
            
            int caches_used = 0;
            for(int i = 0; i < nr_caches; i++)
                if(!videosInCache[i].isEmpty())
                    caches_used++;
            
            out.println(caches_used);
            for(int i = 0; i < caches_used; i++)
                if(!videosInCache[i].isEmpty()){
                    out.print(i);
                    for(int j = 0; j < videosInCache[i].size(); j++)
                        out.print(" " + videosInCache[i].get(j));
                    out.println();
                }
            
            out.close();
            in.close();
            
            
        }catch(IOException e){
            System.out.println("Error.");
        }
    }
    
}
