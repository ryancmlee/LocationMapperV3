package LatLongParser;


import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import LocationMapper.Log;
import diewald_shapeFile.files.shp.shapeTypes.ShpPolygon;


public class Area implements Serializable
{
	public static boolean doCommpression = false;
	
	public String officialName;
	public Column column;
	public ArrayList<Area> subAreas;  //NOTE - does not check to make sure the sub area is fully contained in this.bbox
    public Area primArea;
	
    public Rectangle2D bBox;
    public float[][][] partsAndPoints;
    private byte[] comressedPartsAndPoints;
    

    
    
    public Area(String officialName, Column column, ShpPolygon shape, Area primArea)
	{
			
		this.officialName = officialName.trim();
		this.column = column;
		this.primArea = primArea;


		this.subAreas = new ArrayList<Area>();

	
		if (shape == null)
		{
			this.bBox = new Rectangle2D.Float(0, 0, 0, 0);
			this.partsAndPoints = null;
		}
		else 
		{
			float[][] corners =  this.convertToFloat(shape.getBoundingBox());
			this.bBox = new Rectangle2D.Float(corners[0][0], corners[1][0], corners[0][1] - corners[0][0], corners[1][1] - corners[1][0]);

			if(doCommpression)
				comressedPartsAndPoints = compressPartsAndPoints(shape);
			else
				this.partsAndPoints = this.convertToFloatAndCutElevation(shape.getPointsAs3DArray());
		}
	}
    
   //returns and area or null
    public Area getFirstAreaWithin(float longitude, float latitude)
	{
		if(longitude == 0 && latitude == 0)
			return null;
		
		int size = this.subAreas.size();
	
		for	(int i = 0; i < size; i++)
			if (this.subAreas.get(i).isPointIn(longitude, latitude))
				return this.subAreas.get(i);
		
		return null;
	}
	
	
    public byte[] compressPartsAndPoints(ShpPolygon shape)
    {
    	byte[] data = null;
    	
    	try
    	{
	    	float[][][] temp = convertToFloatAndCutElevation(shape.getPointsAs3DArray());
	    	
	    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    	GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
	    	ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
	    	objectOut.writeObject(temp);
	    	objectOut.close();
	    	data = baos.toByteArray();
	    }
		catch (Exception e)
		{
			Log.log("ERROR in compressPartsAndPoints:" + e);
		}
    	
    	return data;
    	
    }
	
    public static float[][][] unCompressPartsAndPoints( byte[] bytes)
    {
    	float[][][] data = null;
    	try
    	{
	    	ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	    	GZIPInputStream gzipIn = new GZIPInputStream(bais);
	    	ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
	    	data = (float[][][]) objectIn.readObject();
	    	objectIn.close();
    	}
    	catch (Exception e)
    	{
    		Log.log("ERROR in unCompressPartsAndPoints:" + e);
    	}
    	
    	
    	return data;
    }
    
    public float[][][] convertToFloatAndCutElevation(double[][][] oData)
	{
		float[][][] nData = new float[oData.length][][]; //float[parts][pointIndex][pointData]
		
		for(int i = 0; i <oData.length;i++)
		{
			int polygoneLength= oData[i].length;
			
			nData[i] = new float[polygoneLength][];
			
			for(int j = 0; j < polygoneLength;j++) //for each polygone
			{
				nData[i][j] = new float[2];     //cut off the end... only need lat and long
				
				for(int k = 0; k < nData[i][j].length;k++) //oData[i][j].length
				{
					nData[i][j][k] = (float)oData[i][j][k];
				}
				
//				if(j % 2 == 0 || j <= 2 || j >= polygoneLength - 3 || polygoneLength < 300)
//					index++;
				
					
			}
		}
		return nData;
	}
//	public float[][][] convertToFloatAndCutElevation(double[][][] oData)
//	{
//		float[][][] nData = new float[oData.length][][]; //float[parts][pointIndex][pointData]
//		
//		for(int i = 0; i <oData.length;i++)
//		{
//			int polygoneLength= oData[i].length;
//			
//			int tempIndex = 0;
//			for(int j = 0; j <polygoneLength;j++)
//			{
//				if(j % 2 == 0 || j <= 2 || j >= polygoneLength - 3 || polygoneLength < 300)
//					tempIndex++;
//			}
//			
//			nData[i] = new float[tempIndex][];
//			
//			for(int j = 0, index = 0; j < polygoneLength;j++) //for each polygone
//			{
//				nData[i][index] = new float[2];     //cut off the end... only need lat and long
//				
//				for(int k = 0; k < nData[i][index].length;k++) //oData[i][j].length
//				{
//					nData[i][index][k] = (float)oData[i][index][k];
//				}
//				
//				if(j % 2 == 0 || j <= 2 || j >= polygoneLength - 3 || polygoneLength < 300)
//					index++;
//				
//					
//			}
//		}
//		return nData;
//	}
	
	
	public float[][] convertToFloat(double[][] oData)
	{
		float[][] nData = new float[oData.length][];
		
		for(int i = 0; i <oData.length;i++)
		{
			
			nData[i] = new float[oData[i].length];
			
			for(int j = 0; j <oData[i].length;j++)
			{
				nData[i][j] = (float)oData[i][j];     //cut off the end... only need lat and long
			}
		}
		return nData;
	}
	
	 public boolean isPointIn(float longitude, float latitude )
     {
		 if (!bBox.contains(longitude,latitude)) // we in this bBox?
			 return false;
		 
		 
		 
		 //doBuffering
		 if(this.partsAndPoints == null)
		 {
			 this.partsAndPoints = Area.unCompressPartsAndPoints(comressedPartsAndPoints);
			 this.comressedPartsAndPoints = null;
		 }
	 
		 for (int i = 0; i < this.partsAndPoints.length;i++) //for each polygon, NOTE not subareas, an area can have multiple polyognes. i.e islands
			 if(this.isPointInShape(longitude, latitude, this.partsAndPoints[i])) //check to see if we are inside
				 return true;
		
		 return false;
     }
	 
	 
	 
	 //confirmed  working - probably need more test cases
     //source: http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html (straight translation)
	 private boolean isPointInShape(float longitude, float latitude, float[][] points)
	 {
		int i,j;
		boolean c = false;
		for(i = 0, j= points.length -1; i < points.length; j = i++)
		{
			if( ((points[i][1] > latitude) != (points[j][1] > latitude)) && (longitude < (points[j][0]-points[i][0]) * (latitude - points[i][1]) / (points[j][1] - points[i][1]) + points[i][0]) )                                         
				c = !c;
		}
		return c;
	 }
	
	 @Override public String toString() 
	 {
	    return "|[" + officialName + ", "+ column + "]|";
	 }

		
	

}






