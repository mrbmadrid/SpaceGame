package hpu.edu.spain.Game;

import static java.awt.Color.BLACK;
import static java.awt.Color.gray;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import hpu.edu.spain.Objects2D.Body.BodyType;
import hpu.edu.spain.Objects2D.Terrain;

public class Background {
	
	List<Terrain> bounds;
	
	final BufferedImage background;
	
	public Background (int width, int height) throws Exception {
	    background = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    bounds = new ArrayList<>();
	    Graphics2D builder = background.createGraphics();
	    builder.setColor(BLACK);
	    builder.fillRect(0, 0, width, height);
	    builder.setColor(Color.WHITE);
	    for(int i = 0; i < 1000; i++){
	    	int x = (int)(Math.random()*width);
	    	int y = (int)(Math.random()*height);
	    	int w = (int)(Math.random()*3+1);
	    	if(Math.random() < .2){
	    		if(Math.random()<.5){
	    			builder.setColor(Color.CYAN);
	    		}else{
	    			builder.setColor(Color.YELLOW);
	    		}
	    	}else{
	    		builder.setColor(Color.WHITE);
	    	}
	    	builder.fillOval(x, y, w, w);
	    }
	    builder.setColor(gray);
	    int x = 0;
	    Random god = new Random();
	    int count = 0; //counter to ensure first section is not buildings
	
	    Color groundDark = new Color(god.nextInt(200)+20, god.nextInt(200)+20, 
	            god.nextInt(200)+20);
	    Color groundLight = groundDark.brighter();
	    
	    //generates bottom terrain
	    while(x < width-800) {
	        
	        int terrain = god.nextInt(5); //random variable for land or buildings
	        
	        //generate land
	        if(terrain > 3 || count < 2){
	            int run = width/16;
	            run = run/40 + 3;
	            int[] yPoints = new int[run];
	            int[] xPoints = new int[run];
	            xPoints[0]=x + width/16 +25;
	            yPoints[0]=height;
	            xPoints[1]= x;
	            yPoints[1]= height;
	            xPoints[2]=x+25;
	            yPoints[2]=height - 50;
	            xPoints[run-1]=x + width/16;
	            yPoints[run-1]=height;
	            for(int i = 3; i < run-1; i++){
	                xPoints[i] = xPoints[i-1]+40;
	            }

	            for(int i = 3; i < run-1; i++){
	                yPoints[i] = height-god.nextInt(50)-25;
	            }
	
	            builder.setColor(groundLight);
	            builder.fillPolygon(xPoints, yPoints, run-1);
	            Polygon bottom = new Polygon(xPoints, yPoints, xPoints.length);
	            Terrain b = new Terrain(x, bottom.getBounds().y, bottom);
	            b.setType(BodyType.STATIC);
	            bounds.add(b);
	            x+=width/16;
	            count++;
	            
	            //Shade section of ground
	            for(int i = 3; i < run-1; i++){
	                if(yPoints[i]<yPoints[i-1]){
	                    builder.setColor(groundLight.brighter());
	                    int[] yShade = {yPoints[i-1], yPoints[i], height};
	                    int[] xShade = {xPoints[i-1], xPoints[i], xPoints[i-1]};
	                    builder.fillPolygon(xShade, yShade, 3);
	                    builder.setColor(groundLight);
	                    int[] yShade1 = {yPoints[i-1], height, yPoints[i]+yPoints[i]/2};
	                    int[] xShade1 = {xPoints[i-1], xPoints[i-1], (xPoints[i-1]+xPoints[i])/2};
	                    builder.fillPolygon(xShade1, yShade1, 3);
	                    int[] yShade2 = {yPoints[i-1], height, height, yPoints[i]+yPoints[i]/2};
	                    int[] xShade2 = {xPoints[i-1], xPoints[i-1], (xPoints[i-1]+xPoints[i])/2,
	                        (xPoints[i-1]+xPoints[i])/2};
	                    builder.fillPolygon(xShade2, yShade2, 4);
	                }
	                else if(yPoints[i]>yPoints[i-1]){
	                    builder.setColor(groundDark.darker());
	                    int[] yShade = {yPoints[i-1], yPoints[i], height};
	                    int[] xShade = {xPoints[i-1], xPoints[i], xPoints[i]};
	                    builder.fillPolygon(xShade, yShade, 3);
	                    builder.setColor(groundDark);
	                    int[] yShade2 = {yPoints[i-1], height, height};
	                    int[] xShade2 = {xPoints[i-1], xPoints[i-1], xPoints[i]};
	                    builder.fillPolygon(xShade2, yShade2, 3);
	                }
	
	            }
	            
	            
	            
	        }
	        //generate buildings
	        else{
	            Color building = new Color(god.nextInt(100)+100, god.nextInt(100)+100,
	                    god.nextInt(100)+100);
	            int foundation = (god.nextInt(5)+10)*10;
	            int buildingHeight = ((god.nextInt(4)+1) * (height/10));
	            int y = height-buildingHeight;
	            int[] topYPoints = {y, y, y-50, y-50};
	            int[] topXPoints = {x,  (x + foundation),(x + foundation + 25),
	               (x + 25)};
	            int[] sideYPoints = {y, y-50, height-50, height};
	            int[] sideXPoints = {(x + foundation), (x + foundation + 25),
	                (x + foundation + 25), (x + foundation)};
	            Terrain b = new Terrain(x, y, 
	            		new Polygon(new int[]{x, x+foundation, x+foundation, x}, 
	            				new int[]{y, y, y+buildingHeight, y+buildingHeight}, 4));
	            b.setType(BodyType.STATIC);
	            bounds.add(b);
	            
	            builder.setColor(building.darker());
	            builder.fill3DRect(x, y, foundation, buildingHeight, true);
	            builder.setColor(building);
	            builder.fillPolygon(topXPoints, topYPoints, 4);
	            builder.setColor(building.brighter());
	            builder.fillPolygon(sideXPoints, sideYPoints, 4);
	            x += foundation;
	            
	            //space between buildings
	            int[] yPoints = new int[8];
	            int[] xPoints = new int[8];
	            xPoints[0]=x + 25;
	            yPoints[0]=height;
	            xPoints[1]= x;
	            yPoints[1]= height;
	            xPoints[2]=x+25;
	            yPoints[2]=height - 50;
	            xPoints[7]=x + 120;
	            yPoints[7]=height;
	            for(int i = 3; i < 7; i++){
	                xPoints[i] = xPoints[i-1]+40;
	            }
	            for(int i = 3; i < 7; i++){
	                yPoints[i] = height-god.nextInt(50)-25;
	                y=yPoints[i];
	            }
	
	            builder.setColor(groundLight);
	            builder.fillPolygon(xPoints, yPoints, 8);
	            Polygon p = new Polygon(xPoints, yPoints, 8);
	            Terrain g = new Terrain(p.getBounds().x, p.getBounds().y, p);
	            g.setType(BodyType.STATIC);
	            bounds.add(g);
	            //shade space between buildings
	            for(int i = 3; i < 7; i++){
	                if(yPoints[i]<yPoints[i-1]){
	                    builder.setColor(groundLight.brighter());
	                    int[] yShade = {yPoints[i-1], yPoints[i], height};
	                    int[] xShade = {xPoints[i-1], xPoints[i], xPoints[i-1]};
	                    builder.fillPolygon(xShade, yShade, 3);
	                    builder.setColor(groundLight);
	                    int[] yShade1 = {yPoints[i-1], height, yPoints[i]+yPoints[i]/2};
	                    int[] xShade1 = {xPoints[i-1], xPoints[i-1], (xPoints[i-1]+xPoints[i])/2};
	                    builder.fillPolygon(xShade1, yShade1, 3);
	                    int[] yShade2 = {yPoints[i-1], height, height, yPoints[i]+yPoints[i]/2};
	                    int[] xShade2 = {xPoints[i-1], xPoints[i-1], (xPoints[i-1]+xPoints[i])/2,
	                        (xPoints[i-1]+xPoints[i])/2};
	                    builder.fillPolygon(xShade2, yShade2, 4);
	                }
	                else if(yPoints[i]>yPoints[i-1]){
	                    builder.setColor(groundDark.darker());
	                    int[] yShade = {yPoints[i-1], yPoints[i], height};
	                    int[] xShade = {xPoints[i-1], xPoints[i], xPoints[i]};
	                    builder.fillPolygon(xShade, yShade, 3);
	                    builder.setColor(groundDark);
	                    int[] yShade2 = {yPoints[i-1], height, height};
	                    int[] xShade2 = {xPoints[i-1], xPoints[i-1], xPoints[i]};
	                    builder.fillPolygon(xShade2, yShade2, 3);
	                }
	            }
	            
	            x+=120;
	        }
	      
	
	
	    }
	    width-=800;
	    int[] yPoints = new int[102];
	    int[] xPoints = new int[102];
	    xPoints[0]=width;
	    yPoints[0]=0;
	    xPoints[1]= 0;
	    yPoints[1]= 0;
	    xPoints[2]=0;
	    yPoints[2]=150;
	    xPoints[101]=width;
	    yPoints[101]=0;
	    for(int i = 3; i < 101; i++){
	        xPoints[i] = xPoints[i-1]+god.nextInt(width/100)+width/200;
	    }
	    for(int i = 3; i < 101; i++){
	        yPoints[i] = (god.nextInt(4)+1) * height/10;
	    }
	
	    builder.setColor(groundLight);
	    builder.fillPolygon(xPoints, yPoints, 102);
	    Polygon p = new Polygon(xPoints, yPoints, 102);
	    Terrain c = new Terrain(p.getBounds().x, p.getBounds().y, p);
	    c.setType(BodyType.STATIC);
	    bounds.add(c);
	    
	    //Shading Code
	    for(int i = 3; i < 101; i++){
	        if(yPoints[i]<yPoints[i-1]){
	            builder.setColor(groundDark.darker());
	            int[] yShade = {yPoints[i-1], yPoints[i], 0, 0, yPoints[i]/2};
	            int[] xShade = {xPoints[i-1], xPoints[i], xPoints[i],
	                (xPoints[i-1]+xPoints[i])/2, (xPoints[i-1]+xPoints[i])/2};
	            builder.fillPolygon(xShade, yShade, 5);
	            builder.setColor(groundDark);
	            int[] yShade1 = {yPoints[i-1], 0, yPoints[i]/2};
	            int[] xShade1 = {xPoints[i-1], xPoints[i-1], (xPoints[i-1]+xPoints[i])/2};
	            builder.fillPolygon(xShade1, yShade1, 3);
	            int[] yShade2 = {yPoints[i-1], 0, 0, yPoints[i]/2};
	            int[] xShade2 = {xPoints[i-1], xPoints[i-1], (xPoints[i-1]+xPoints[i])/2,
	                (xPoints[i-1]+xPoints[i])/2};
	            builder.fillPolygon(xShade2, yShade2, 4);
	        }
	        else if(yPoints[i]>yPoints[i-1]){
	            builder.setColor(groundLight.brighter());
	            int[] yShade = {0, yPoints[i-1], yPoints[i], yPoints[i-1]/2};
	            int[] xShade = {xPoints[i-1], xPoints[i-1], xPoints[i], (xPoints[i-1]+xPoints[i])/2};
	            builder.fillPolygon(xShade, yShade, 4);
	            builder.setColor(groundLight);
	            int[] yShade2 = {yPoints[i], 0, yPoints[i-1]/2};
	            int[] xShade2 = {xPoints[i], xPoints[i], (xPoints[i-1]+xPoints[i])/2};
	            builder.fillPolygon(xShade2, yShade2, 3);
	        }
	    }
	}
  
	public BufferedImage getImage(){
		return background;
	}
	
	public List<Terrain> getBounds(){
		return bounds;
	}
}


