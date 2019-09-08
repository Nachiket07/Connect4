import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
// you can play with machine with two levels level 0 : easy level 1: hard
// when playing with easy level server will generate random no and will insert accordingly
// when playing with hard there are 3 cases they are written (algorithm) in server program
// server will pop up check 1,2,3 etc but ignore them they are written to check flow of the program !
public class Connect4client  
{
	static int board[][];
	static int size;
	static void show()
	{
		//System.out.println("xyz !");
		int i,j;
		System.out.println();
		for(i=0;i<size;i++)
		{
			for(j=0;j<size;j++)
			{
				if(board[i][j]==0)
				{
					System.out.print(" "+"-");
				}
				else
				{
					if(board[i][j]==3)
					{
						System.out.print(" "+"R");
					}
					else
					{
						System.out.print(" "+"Y");
					}
				}
				//System.out.print(" "+board[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}
	static int entryToBoard(int column,int user)  // if returns 1 then 
	{// element is inserted if 0 then that column is already full !
	// aka no insertion.
		int i;
		for(i=size-1;i>=0;i--)
		{
			if(board[i][column]==0)
			{
				board[i][column]=user;
				return 1;
			}
		}
		return 0;
	}
	/*static void entryFromOtherSide(int column,int user)
	{
		int i;
		for(i=size-1;i>=0;i--)
		{
			if(board[i][column]==0)
			{
				board[i][column]=user;
			}
		}
	}*/
		
	static boolean checkWin(int user)
	{// i times j is firest element of horizontal check box and k goes way back !
		int i,j,k,l,sum_row_wise=0,sum_col_wise=0,four,sum_diagonal_one=0,sum_diagonal_two=0; // a goes from k to k-4
		for(i=size-1;i>=0;i--) // controls rows
		{
			for(j=size-1;j>=3;j--) // controls starting sum.
			{
				k=j;             // controls four cell that are to be added
				four=4;
				sum_row_wise=0;
				sum_col_wise=0;
				while(four>0)
				{
					sum_row_wise+=board[i][k];
					sum_col_wise+=board[k][i];
					k--;
					four--;
				}
				if(sum_col_wise==4*user || sum_row_wise==4*user)
				{ 
					if(sum_col_wise==4*user)
					{
						System.out.println("Due to col check !");
					}
					else
					{
						System.out.println("Due to row check !");
					}
					return true;
				}
			}
		}
		// diagonal 1 check !
		//diagonal 1 is like first diagonal of square 
		// diagonal 2 is like second diagonal of square
		for(i=size-1;i>=3;i--)
		{
			for(j=size-1;j>=3;j--)
			{
				l=i;
				k=j;
				four=4;
				sum_diagonal_one=0;
				//sum_diagonal_two=0;
				while(four>0)
				{
					sum_diagonal_one+=board[l][k];                                                          
					//sum_diagonal_two+=board[k][l];
					k--;
					l--;
					four--;
				}
				if(sum_diagonal_one==4*user)
				{
					System.out.println("due to column check !");
					return true;
				}
			}
		}
		for(i=size-1;i>=3;i--)
		{
			for(j=0;j<size-3;j++)
			{
				l=i;
				k=j;
				four=4;
				sum_diagonal_two=0;
				while(four>0)
				{
					sum_diagonal_two+=board[l][k];
					k++;
					l--;
					four--;
				}
				if(sum_diagonal_two==4*user)
				{
					return true;
				}
			}
		}
		return false;
	}
		
	static boolean boardCheck()
	{
		int i;
		for(i=0;i<size;i++)
		{
			if(board[0][i]==0)
			{
				return true;
			}
		}
		return false;
	}
		
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		Scanner sc=new Scanner(System.in);
		System.out.println("Enter i/p address of Server to begin game ");
		String ip=sc.next();
		
		try(Socket socket=new Socket(ip,5000))
		{
			BufferedReader B= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter p=new PrintWriter(socket.getOutputStream(),true);
			
			
			int cturn,turn,user1,user2,column=0,difficulty=0;
			//
			System.out.println("Enter 0 if you want to play with human friend !");
			System.out.println("Enter 1 if you want to play with machine !");
			int fri_mac=sc.nextInt();
			if(fri_mac==1)
			{
				System.out.println("Enter difficulty level enter 0 if easy enter 1 if hard !");
				System.out.println("Enter 2 for invincible difficulty !");
				difficulty=sc.nextInt();
			}
			System.out.println("Enter which turn you want ");
			System.out.println("Enter 0 if first turn Enter 1 if second turn ");
			turn=sc.nextInt();
			//
			
			System.out.println("enter size of board ");
			System.out.println("Minimum size is 4");
			while(true)
			{
				size=sc.nextInt();
				if(size>=4)
				{
					break;
				}
				else
				{
					System.out.println("enter valid size (greater than 4)");
				}
			}
			board=new int[size][size];
			System.out.println("User1 select red or yellow ");
			System.out.println("Enter 3 to choose red(R) ");
			System.out.println("enter 5 to choose yellow(Y)");
			while(true)
			{
				user1=sc.nextInt();
				if(user1==3 || user1==5)
				{
					user2=8-user1;
					break;
				}
				else
				{
					System.out.println("Wrong choice ! Enter choice again");
					//System.out.println();
				}
			}	
			//turn=0;
			int success;
			int win_status;
			System.out.println("value of column starts from 0 to 'size -1' ");
			System.out.println("so be careful !");
			// now send server which color have you chosen 3 or 5 and send server board size and its turn;
			
			p.println(1-turn);
			p.println(user1);
			p.println(user2);
			p.println(size);
			p.println(fri_mac); 
			if(fri_mac==1)
			{
				p.println(difficulty);
			}
			//yo
			
			
			while(boardCheck())
			{
				success=0;
				show();
				if(turn==0)
				{
					if(column!=size+1)
					{
						while(success==0)
						{
							System.out.println("Client enter column in which you want to insert ball  !");
							column=sc.nextInt();
							if(column < size && column >=0)
							{
								success=entryToBoard(column,user1);
							}	
							else
							{
								System.out.println(" invalid I/P or row should be full !");
							}
						
						}	
						turn=1;
						// send server that its his turn now by sending turn variable;
						p.println(column);
						//p.println(turn);  // in the end set turn
						// yo
					}
					if(checkWin(user1) || column==size+1)
					{
						show();
						if(column==size+1)
						{	
							System.out.println("Server has given up ! cheers !");
						}
						System.out.println("Congo client ! you have won !");
						System.exit(0);
					}
				}	
				else
				{
					System.out.println("Wait its servers turn !");
					column=Integer.parseInt(B.readLine());
					if(column!=size+1)  // if client surrenders then no need to make entry on the board !
					{
						entryToBoard(column, user2);
					}
					if(checkWin(user2))
					{
						show();
						System.out.println("Sorry client ! you lost game !");
						System.exit(0);
						
					}
					turn=0;
				}		
			}	
			System.out.println("Nothing to play match darws!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Sorry its wrong i/p ");
			System.out.println("If you still think its correct then may be ");
			System.out.print("Server is off try other server !");
		}	
	}
}

