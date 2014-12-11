/*
	23/09/11 - Rafael Mota
	26/09/11 - George Harinson
	05/10/11 - João Marcelo
 */
import java.io.*;

public class Evaluation
{
	private SubEvaluation[] OL; // cada uma das cargas oferecidas
	private int nOL; // quantidade de cargas oferecidas
	private String rede; // nome da rede
	private String trafficDistri; // distribuição espacial do tráfego adotada
	private String pathTst; // path do teste
	private String[] pathOL; // path das cargas oferecidas
    private int dimX; // tamanho da rede no eixo X
    private int dimY; // tamanho da rede no eixo Y
    private String topology; // topologia da rede
    private String graphsPath;

	public Evaluation( String nome, String pth,String graphsPath, int dX, int dY)
	{
		this.graphsPath = graphsPath;
		// dimensões da rede
		this.dimX = dX;
		this.dimY = dY;
		// topologia da rede
		this.topology = "MESH";
		// nome da rede
		this.rede = nome;
		// distribuição de tráfego
		this.trafficDistri = "UNIFORME";
		// path principal
		pathTst = pth;
		// lista pastas (subtestes)
		File folder = new File( pathTst );
		pathOL = folder.list();
		//seta quantidade de subtestes
		nOL = pathOL.length;
		// aloca OL
		OL = new SubEvaluation[ nOL ];
		// aloca e inicializa cada OL[i]
		for( int i = 0; i < nOL; i++ )
			OL[i] = new SubEvaluation( pathTst,graphsPath,rede, pathOL[i], dimX, dimY, rede );
	}

	/* gera os arquivos 'Distribuição espacial' de um determinado tipo */
	public void makeDistris( char tipo )
	{
		for( int i = 0; i < nOL; i++ )
		{
			OL[i].makeSpatDistriLat( tipo );
			OL[i].makeSpatDistriLatN( tipo );
			OL[i].makeSpatDistriAccepTraff( tipo );
            OL[i].makeHistogramAccepTraff( tipo );
            //OL[i].printRetransmissions();
		}
	}
	
	/* gera os arquivos 'Distribuição espacial' necessários */
	public void makeDistris()
	{
		boolean H = false;
		for( int i = 0; i < nOL; i++ )
			if( OL[i].getNPck( 'H' ) != 0 )
			{
				H = true;
				break;
			}
		if( H == true )
		{
			makeDistris( 'T' );
			makeDistris( 'H' );
			makeDistris( 'L' );	
		}
		else
			makeDistris( 'T' );
	}
	
	public void plotDistris( char tipo )
	{
		for( int i = 0; i < nOL; i++ )
		{
			OL[i].plotSpatDistriLat( tipo );
			OL[i].plotSpatDistriLatN( tipo );
			OL[i].plotSpatDistriAccepTraff( tipo );
                        OL[i].plotSpatDistriAccepTraff3D( tipo );
		}
	}
	
	public void plotDistris()
	{
		boolean H = false;
		for( int i = 0; i < nOL; i++ )
			if( OL[i].getNPck( 'H' ) != 0 )
			{
				H = true;
				break;
			}
		if( H == true )
		{
			plotDistris( 'T' );
			plotDistris( 'H' );
			plotDistris( 'L' );	
		}
		else
			plotDistris( 'T' );
	}
	
	/* geta a quantidade de tráfegos oferecidos */
	public int getNOL()
	{
		return nOL;
	}

	/* Gera o arquivo para a confecção do CNF de Latência Pura */
	public void makeCNFLat( char tipo )
	{
		double offerload[] = new double[ nOL ];
		double latmean[] = new double[ nOL ];
		for( int i = 0; i < nOL; i++ )
		{
			offerload[i] = OL[i].getOL()/100.0;
			latmean[i] = OL[i].getLatMean( tipo );
		}
		HandleFiles hand = new HandleFiles();
		hand.WriteFile( graphsPath + "//result_"+rede+"//", "CNF_Lat", offerload, latmean, nOL );
	}

	/* Gera o arquivo para a confecção do CNF de Latência Normalizada */
	public void makeCNFLatN( char tipo )
	{
		double offerload[] = new double[ nOL ];
		double latNmean[] = new double[ nOL ];
		for( int i = 0; i < nOL; i++ )
		{
			offerload[i] = OL[i].getOL()/100.0;
			latNmean[i] = OL[i].getLatNMean( tipo );
		}
		HandleFiles hand = new HandleFiles();
		hand.WriteFile( graphsPath + "//", "CNF_Latn" + tipo+rede, offerload, latNmean, nOL );
	}

	/* Gera o arquivo para a confecção do CNF de Tráfego Aceito */
	public void makeCNFAccepTraff( char tipo )
		{
		double offerload[] = new double[ nOL ];
		double accepTraffmean[] = new double[ nOL ];
		for( int i = 0; i < nOL; i++ )
		{
			offerload[i] = OL[i].getOL()/100.0;
			accepTraffmean[i] = OL[i].getAccepTraffMean( tipo );
		}
		HandleFiles hand = new HandleFiles();
		//System.out.println("@@"+rede);
		
		hand.WriteFile( graphsPath + "//result_"+rede+"//", "CNF_AT", offerload, accepTraffmean, nOL );
	}

	/* Gera os arquivos para a confecção dos CNF's de um determinado tipo */
	public void makeCNFs( char tipo )
	{
			File dir = new File(graphsPath+"//result_"+rede);
			dir.mkdir();
			
			makeCNFLat( tipo );
			//makeCNFLatN( tipo );
			makeCNFAccepTraff( tipo );
	}
	
	/* Gera os arquivos para confecção dos CNF's necessários */
	public void makeCNFs()
	{
		boolean high = false;
		for( int i = 0; i < nOL; i++ )
			if( OL[i].getNPck( 'H' ) != 0)
				{
					high = true;
					break;
				}
		if ( high == true )
		{
			makeCNFs( 'T' );
			makeCNFs( 'H' );
			makeCNFs( 'L' );
		}
		else
			makeCNFs( 'T' );
		
	}
	
	public void plotCNFLat( char tipo )
	{
		PlotGraphics.plotCNF_Lat( graphsPath + "//result_"+rede+"//", "CNF_Lat", rede );
	}
		
	public void plotCNFLatN( char tipo )
	{
		PlotGraphics.plotCNF_LatN( graphsPath + "//", "CNF_Latn" + tipo+rede, rede );
	}

	public void plotCNFAccepTraff( char tipo )
	{
		PlotGraphics.plotCNF_AT( graphsPath + "//result_"+rede+"//", "CNF_AT", rede );
	}
	
	public void plotCNFs( char tipo )
	{
		plotCNFLat( tipo );
		//plotCNFLatN( tipo );
		plotCNFAccepTraff( tipo );		
	}
	
	public void printRetrans()
	{
		for( int i = 0; i < nOL; i++ )
		{			
			OL[i].printRetransmissions(graphsPath);
			
		}
	}
	
	public void plotCNFs()
	{
		boolean high = false;
		for( int i = 0; i < nOL; i++ )
			if( OL[i].getNPck( 'H' ) != 0)
				{
					high = true;
					break;
				}
		if ( high == true )
		{
			plotCNFs( 'T' );
			plotCNFs( 'H' );
			plotCNFs( 'L' );
		}
		else
			plotCNFs( 'T' );
	}
	
	/* Gera os relatórios de cada subteste */
	public void makeRelats()
	{
		for( int i = 0; i < nOL; i++ )
			OL[i].makeRelat();
	}
}