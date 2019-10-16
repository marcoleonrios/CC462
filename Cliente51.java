package redesOk;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import redesOk.TCPClient50;

class Cliente51 {

    public int sum[] = new int[40];
    
    public int lista_de_imagenes[][][] = new int[10000][][]; 
    String[] pathnames;
    TCPClient50 mTcpClient;
    Scanner sc;
    
    public static void main(String[] args) {
        Cliente51 objcli = new Cliente51();
        objcli.iniciar();
    }

    void iniciar() {
        new Thread(
            new Runnable() {
	            @Override
	            public void run() {
	                mTcpClient = new TCPClient50("127.0.0.1",
	                    new TCPClient50.OnMessageReceived() {
		                    @Override
		                    public void messageReceived(String message) {
		                        ClienteRecibe(message);
		                    }
	                	}
	                );
	                mTcpClient.run();
	            }
            }
        ).start();
        //---------------------------

        String salir = "n";
        sc = new Scanner(System.in);
        System.out.println("Cliente bandera 01");
        while (!salir.equals("s")) {
            salir = sc.nextLine();
            ClienteEnvia(salir);
        }
        System.out.println("Cliente bandera 02");

    }

    void ClienteRecibe(String llego) {
        System.out.println("CLINTE50 El mensaje::" + llego);
        if (llego.trim().contains("evalua")) {
            String arrayString[] = llego.split("\\s+");
            int min = Integer.parseInt(arrayString[1]);
            int max = Integer.parseInt(arrayString[2]);

            System.out.println("el min:" + min + " el max:" + max);
            procesar(min, max);
        }
    }

    void ClienteEnvia(String envia) {
        if (mTcpClient != null) {
            mTcpClient.sendMessage(envia);
        }
    }

   
    void procesar(int a, int b) {
    	
    	
    	long time_start, time_end;
    	time_start = System.currentTimeMillis();
    	
    	
        int N = (b - a);//14;
        int H = 120;//luego aumentar
        int d = (int) ((N) / H);
        Thread todos[] = new Thread[200];
        for (int i = 0; i < (H - 1); i++) { 
            todos[i] = new tarea0101((i * d + a), (i * d + d + a), i);
            todos[i].start();
        }
        
        todos[H - 1] = new tarea0101(((d * (H - 1)) + a), (b + 1), H - 1);
        todos[H - 1].start();
        for (int i = 0; i <= (H - 1); i++) {//AQUI AQUI VER <=
            try {
                todos[i].join();
            } catch (InterruptedException ex) {
                System.out.println("error" + ex);
            }
        }
        
             
        time_end = System.currentTimeMillis();
        System.out.println("LA TAREA SE DEMORO: "+ ( time_end - time_start )/1000 +" segundos");
        
       
    }

    public class tarea0101 extends Thread {

        public int max, min, id;
        private BufferedImage outputlists = null;

        tarea0101(int min_, int max_, int id_) {
            max = max_;
            min = min_;
            id = id_;

        }

        public void run() {        
        File f = new File("C:/Users/espin/OneDrive/Escritorio/imagenesparcial");
        int[][] myImg;
        int[][] myImgGS;
        // Populates the array with names of files and directories
        pathnames = f.list();        
        for(int i = min; i < max ; i++){        	
            myImg = LoadImage.getMatrixOfImage("C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[i-1]);
            myImgGS = LoadImage.toGrayScale(myImg, false);
            LoadImage.saveImageFromMatrix(myImgGS,"C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[i-1]+"_converted.jpg");
            
            //CONVOLUCIÓN  
            int hx = 2; ////EN CUENTAS FILAS DIVIDO LA IMAGEN?
            int hy = 2; ////EN CUENTAS COLUMNAS DIVIDO LA IMAGEN?
            
            //int lista_de_imagenes[][][] = new int[1000][][];               
            
            for(int h = 0; h < 4 ; h++) {  //RELLENO EL ARRAY QUE CONTENDRA LAS IMAGENES MODIFICAS POR EL KERNEL          	
            	lista_de_imagenes[h] = LoadImage.getMatrixOfImage("C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[i-1]);
        	}         
            
            int nro_filas = 0 ;
            int nro_columnas = 0 ;        
            Thread hilos[][] = new Thread[1000][1000];
        	nro_filas = myImgGS.length;
        	nro_columnas = myImgGS[0].length;	        	
    		
			try {
				outputlists = ImageIO.read(new File("C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[i-1]+"_converted.jpg"));
			} catch (IOException e) {					
				e.printStackTrace();
			}
			
        	//System.out.println("OPERANDO IMAGEN: "+ (i-1)); 
        	
        	for(int fila = 0; fila < hx ; fila++) {
        		for(int columna = 0; columna < hy ; columna++) {
	        		hilos[fila][columna] = new convolucion(fila,columna, hx*hy, nro_filas, nro_columnas, outputlists);
	        		hilos[fila][columna].start();
        		}
        	}
	        
        	//System.out.println("UNIENDO HILOS.... ");            
             
        	
        	//System.out.println("JOIN IMAGEN: "+(i-1)); 
        	//DOBLE FOR PARA RECORRER LA IMAGEN DIVIDIDA
        	for(int fila = 0; fila < hx ; fila++) {
        		for(int columna = 0; columna < hy ; columna++) {
        			try {           			  
        				hilos[fila][columna].join();
                    } catch (InterruptedException ex) {
                        System.out.println("error" + ex);
                      }
        		}
        	}
	        	         
        	         	
	        
	        for(int h = 0; h < hx*hy ; h++) {
	        	//System.out.println("Guardando imagen: "+(i-1)+" Kernel: "+h);
		        LoadImage.saveImageFromMatrix(lista_de_imagenes[h],"C:/Users/espin/OneDrive/Escritorio/imagenesparcial/"+pathnames[i-1]+"_Kernel_"+h+".jpg");
        	}
	        	
	        
            
            
            
            }
            

        }
    }
    
    //HILOS DE CONVOLUCIÓN
    public class convolucion extends Thread {
    	public int[][] imagen;   
    	public BufferedImage input;
    	public int fila,columna,p,h,k,n,x,y,nro_filas,nro_columnas;
    	public double[][][] kernel = { //kernel blur
    			{
					{0.0625,0.125,0.0625},
	    			{0.125,0.25,0.125},
	    			{0.0625,0.125,0.0625}
    			},
    			{
	    			{0.2,0,0,0,0},
	    			{0,0.2,0,0,0},
	    			{0,0,0.2,0,0},
	    			{0,0,0,0.2,0},
	    			{0,0,0,0,0.2}
    			},
    			{
	    			{-1,-1,-1},
	    			{-1,8,-1},
	    			{-1,-1,-1}
    			},
    			{
	    			{0,-1,0},
	    			{-1,5,-1},
	    			{0,-1,0}
    			}
    			
    			
    	};
    	convolucion(int fila_, int columna_, int partes_, int nro_filas_, int nro_columnas_, BufferedImage input_) {
            this.fila = fila_;
            this.columna = columna_;
            this.nro_filas = nro_filas_;
            this.nro_columnas = nro_columnas_;
            this.input = input_;
            this.p = partes_;
        }
        public void run() { 
        	//CALCULANDO LOS LIMITES DE LA SUBMATRIZ DE LA IMAGEN A OPERAR
        	
        	double minX = columna*nro_filas/Math.sqrt(p);
        	double maxX = minX + nro_filas/Math.sqrt(p);
        	double minY = fila*nro_columnas/Math.sqrt(p);
        	double maxY = minY + nro_columnas/Math.sqrt(p);
        	//System.out.println("hilo: "+h+" minX: "+minX+" maxX: "+maxX+" minY:"+minY+" maxY: "+maxY);
        	///////////////////////////////////////////////////////////////
        	for (k = 0 ; k < 4; k++) { // UNA ITERACION POR KERNEL
        		int sum = 0;  
	        	for(x = (int) minX; x < maxX ; x++) { // RECORRE FILAS DE LA SUBDIVISION DE LA IMAGEN
	        		for(y = (int) minY; y < maxY ;y++) { // RECORRE PIXELES DE DICHAS FILA
	        			
	        	        	  
	        	        	float red=0f,green=0f,bleu=0f;
	        				 
	        				
	        	        	for (int a = 0; a < kernel[k].length ;a++) {// RECORRE FILAS DEL KERNEL K  
	        	    			for(int b = 0; b < kernel[k].length ; b++) {//RECORRE CADA ELEMENTO DE DICHA FILA
	        	    				int submatrizX = (x - kernel[k].length/2 + a + nro_filas) % nro_filas; //el % soluciona los bordes agregando los pixeles del lado opuesto
	        	    				int submatrizY = (y - kernel[k].length/2 + b + nro_columnas) % nro_columnas; 
	        	    				
	        	    				int RGB = input.getRGB(submatrizX,submatrizY);    
	        	
	        	    				int R = (RGB >> 16) & 0xff; // Red Value
	        						int G = (RGB >> 8) & 0xff;	// Green Value
	        						int B = (RGB) & 0xff;		// Blue Value
	        	
	        						red += (R*kernel[k][a][b]);
	        						green += (G*kernel[k][a][b]);
	        						bleu += (B*kernel[k][a][b]);	   
	        	    			}
	        	    		}         	
	        	        	
	        	        	int outR, outG, outB;
	        				
	        				outR = Math.min(Math.max((int)red,0),255);
	        				outG = Math.min(Math.max((int)green,0),255);
	        				outB = Math.min(Math.max((int)bleu,0),255);
	        				
	        				Color pixelcolor = new Color(outR, outG, outB);
	        				sum = pixelcolor.getRGB();
	        	        	//System.out.println("k: "+k+" x: "+x+" y: "+y );
	        				lista_de_imagenes[k][x][y] = sum;
	                	
	        		}
	        	}
	        	
        	}
        	
        }
    }

}
