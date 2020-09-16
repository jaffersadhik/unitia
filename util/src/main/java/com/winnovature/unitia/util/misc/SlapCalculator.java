package com.winnovature.unitia.util.misc;

public class SlapCalculator {

	public int getSlap(long difference){
		
		if(difference<1){
			
			return 1;
			
		}else if(difference<2){
			
			return 2;
			
		}else if(difference<3){
			
			return 3;
			
		}else if(difference<4){
			
			return 4;
			
			
		}else if(difference<5){
			
			return 5;
			
		}else if(difference<10){
			
			return 6;
			
		}else if(difference<20){
			
			return 7;
			
		}else if(difference<30){
			
			return 8;
			
		}else if(difference<40){
			
			return 9;
			
		}else if(difference<50){
			
			return 10;
			
		}else if(difference<60){
			
			return 11;
			
		}else if(difference<120){
			
			return 12;
			
		}else if(difference<180){
			
			return 13;
			
		}else if(difference<240){
			
			return 14;
			
			
		}else if(difference<300){
			
			return 15;
			
		}else if(difference<600){
			
			return 16;
			
		}else if(difference<900){
			
			return 17;
			
		}else if(difference<1200){
			
			return 18;
			
		}else if(difference<1800){
			
			return 19;
			
		}else{			
			return 20;
		}
	}
}
