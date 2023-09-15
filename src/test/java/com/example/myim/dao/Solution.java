package com.example.myim.dao;
import java.util.*;

public class Solution {
    PriorityQueue<Integer> maxSet = new PriorityQueue<>((a,b)->Integer.compare(a,b));//>
    PriorityQueue<Integer> minSet = new PriorityQueue<>((a,b)->Integer.compare(b,a));//<=
    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] res = new double[nums.length-k+1];
        int pos = 0;

        for(int i = 0;i <= k-1;i++){
            if(i == 0){
                minSet.add(nums[i]);
                continue;
            }

            addQ(nums[i]);
            adjust();


        }

        if((minSet.size() + maxSet.size()) % 2 == 0){
            res[pos++] = (minSet.peek()/2.0 + maxSet.peek()/2.0) ;
        }else{
            res[pos++] = minSet.peek() * 1.0;
        }
        int left = 0;
        int right = k-1;
        while(right < nums.length-1){

            right++;
            sub(nums[left]);
            addQ(nums[right]);
            adjust();
            left++;
            if((minSet.size() + maxSet.size()) % 2 == 0){
                res[pos++] = (minSet.peek()/2.0 + maxSet.peek()/2.0);
            }else{
                res[pos++] = minSet.peek() * 1.0;
            }
            System.out.println(minSet);
            System.out.println(maxSet);
        }
        return res;
    }

    public void sub(int i){
        if(i > minSet.peek()){
            maxSet.remove(i);
        }else{
            minSet.remove(i);
        }
    }

    public void addQ(int i){
        // System.out.println(i);
        if(minSet.isEmpty() ||i <= minSet.peek()){
            minSet.add(i);

        }else{
            maxSet.add(i);

        }
    }

    public void adjust(){
        while(minSet.size()-maxSet.size() >= 2){
            int temp = minSet.poll();
            maxSet.add(temp);
        }
        while(maxSet.size() > minSet.size()){
            int temp = maxSet.poll();
            minSet.add(temp);
        }
    }

    public static void main(String[] args) {
        Solution s = new Solution();
        double[] doubles = s.medianSlidingWindow(new int[]{2147483647, 1, 2, 3, 4, 5, 6, 7, 2147483647}, 2);
    }
}