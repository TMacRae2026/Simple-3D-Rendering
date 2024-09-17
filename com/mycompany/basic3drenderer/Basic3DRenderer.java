/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.basic3drenderer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author tyler
 */
public class Basic3DRenderer {
   
   
    public static void main(String[] args) {
        JFrame frame = new JFrame("Basic 3D rendering engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);
       
        Container pane = frame.getContentPane();
       
        JPanel renderPanel;
        renderPanel = new JPanel() {
            int key = 0;
            int curLetter = 0;
           
            String quote = "Out of all the clutter, find simplicity -- Albert Einstein";
           
            String curString = "";
           
            int rotAmmount = 0;
           
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                //Make our 3D object as a list of triangles
                List triangles = new ArrayList<>();
                triangles.add(new Triangle(new Vertex(70,70,70),
                                            new Vertex(-70, -70, 70),
                                            new Vertex(-70, 70, -70)));
                triangles.add(new Triangle(new Vertex(70,70,70),
                                            new Vertex(-70, -70, 70),
                                            new Vertex(-70, -70, 70)));
                triangles.add(new Triangle(new Vertex(-70, 70, -70),
                                            new Vertex(70, -70, -70),
                                            new Vertex(70, 70, 70)));
                triangles.add(new Triangle(new Vertex(-70, 70, -70),
                                            new Vertex(70, -70, -70),
                                            new Vertex(-70, -70, 70)));
              
                g.setColor(Color.white);
                int offsetX = getWidth() / 2;
                int offsetY = getHeight() / 2;
               
                //init a transform matrix
                double rotAmmountR = Math.toRadians(rotAmmount);
                Matrix3 transform;
                transform = getPitchYawTrans(rotAmmountR, rotAmmountR / 3);
                
                rotAmmount++;
                
                for(Object o : triangles) {
                    Triangle t = (Triangle) o;
                    /*g.drawLine(t.v1.x + offsetX, t.v1.y + offsetY, t.v2.x + offsetX, t.v2.y + offsetY); //This is tecnically projection but has no matrix applications
                    g.drawLine(t.v1.x + offsetX, t.v1.y + offsetY, t.v3.x + offsetX, t.v3.y + offsetY);
                    g.drawLine(t.v2.x + offsetX, t.v2.y + offsetY, t.v3.x + offsetX, t.v3.y + offsetY);*/
                   
                    //apply transforms to vertexes from matrix
                    Vertex v1 = transformMatrix(t.v1, transform.getMatrix());
                    Vertex v2 = transformMatrix(t.v2, transform.getMatrix());
                    Vertex v3 = transformMatrix(t.v3, transform.getMatrix());
                   
                    //finally draw the outline
                    g.setColor(new Color(0x008108));
                    g.drawLine(v1.x + offsetX, v1.y + offsetY, v2.x + offsetX, v2.y + offsetY);
                    g.drawLine(v1.x + offsetX, v1.y + offsetY, v3.x + offsetX, v3.y + offsetY);
                    g.drawLine(v2.x + offsetX, v2.y + offsetY, v3.x + offsetX, v3.y + offsetY);
                   
                }
               
               
               
                g.setColor(new Color(0x00F108));
                key++;
               
                if(key / 3 >= 1) {
                    key = 0;
                    if(quote.length() <= curLetter) {
                       
                    } else {
                        curString += quote.charAt(curLetter);
                        curLetter++;
                    }
                   
                }
               
               
                g.drawString(curString, getWidth() / 2 - 135, getHeight() / 2 - 180);
               
             }
        };
        pane.add(renderPanel, 0);
       
        frame.setContentPane(pane);
        frame.setVisible(true);
        while(true){
            try{
                Thread.sleep(5);
            }
            catch(Exception e){
                e.printStackTrace();
            }
           
           
            renderPanel.repaint();
        }
    }
   
    static Vertex transformMatrix(Vertex v, double[][] m) {
        return new Vertex(
        (int)(v.x * m[0][0] + v.y * m[1][0] + v.z * m[2][0]),
        (int)(v.x * m[0][1] + v.y * m[1][1] + v.z * m[2][1]),
        (int)(v.x * m[0][2] + v.y * m[1][2] + v.z * m[2][2]));
    }
    
    static Matrix3 getPitchYawTrans(double pitch, double yaw) {
        double[][] matrixXZ = {
                    {Math.cos(yaw), 0, -Math.sin(yaw)},
                    {0, 1, 0},
                    {Math.sin(yaw), 0, Math.cos(yaw)}
                };
        Matrix3 transform = new Matrix3();
        transform.setMatrix(matrixXZ);
       
        double[][] MatrixTemp = new double[][] {
            {1, 0, 0},
            {0, Math.cos(pitch / 2),Math.sin(pitch / 2)},
            {0, -Math.sin(pitch / 2),Math.cos(pitch / 2)}};
        Matrix3 matrixPitch = new Matrix3();
        matrixPitch.setMatrix(MatrixTemp);
        transform = transform.multiply(matrixPitch);
        return transform;

    }
   
   
}

/*Vertexes are just points in 3D space (think vector3)
    All we need for a triangle is 3 vertexes*/
class Vertex {
    int x;
    int y;
    int z;
    Vertex(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

class Triangle {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Triangle(Vertex v1, Vertex v2, Vertex v3) {
        this.v1 = v1;
        this.v2 = v2; //9 floats per triangle
        this.v3 = v3;
    }
   
}

class Matrix3 {
    double[][] matrix;
   
    public Matrix3() {
        matrix = new double[3][3];
    }
   
    public void setMatrix(double[][] matrix){
        this.matrix = matrix;
    }
   
    public double[][] getMatrix() {
        return matrix;
    }
    public Matrix3 multiply(Matrix3 other) {
        Matrix3 result = new Matrix3();
        double[][] otherMatrix = other.getMatrix();
        double[][] resultMatrix = new double[3][3];
       
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                resultMatrix[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                    resultMatrix[i][j] += this.matrix[i][k] * otherMatrix[k][j];
                }
            }
        }
        result.setMatrix(resultMatrix);
        return result;
       
    }
}

/*PLAN FOR MAKING THE OBJECT ROTATE!
After a couple google searches and a few questions to ChatGPT I figured out that the easiest way to
rotate Vertexes like this is with Linear Algebra. This is going to take alot of googling and ALOT math:
[cos, 0,    -sin] XY rotation
[0,   1,    0   ]
[sin, 0,    cos ]

apply transformations to a vertex (from matrix above):
x = x * cos + y * 0 + z * -sin
y = x * 0 + y * 1 + z * 0
z = x * cos + y * 0 + z * sin

NOTE: USE RADIANS NOT DEGREES!
*/
