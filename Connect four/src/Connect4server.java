import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Connect4server  
{
	static int board[][];
	static int size;
	static int user1;
	static int invincible[];   // used only when difficulty is invincible for optimum position !
	static PrintWriter p;
	static int []busyrows;
	static int surrender;  // if takes value size+1 then server surrenders !
	static int entryToBoardUser(int column)  // if returns 1 then 
	{// element is inserted if 0 then that column is already full !
	// aka no insertion. this is used for user entey 
	// Special function for invincible difficulty !
		int i;
		for(i=size-1;i>=0;i--)
		{
			if(board[i][column]==0)
			{
				board[i][column]=8-user1;
				return 1;
			}
		}
		return 0;
	}
	static int entryToBoardMachine(int column)  // if returns 1 then 
	{// element is inserted if 0 then that column is already full !
	// aka no insertion.
		int i;
		for(i=size-1;i>=0  ;i--)
		{
			if(board[i][column]==0)
			{
				board[i][column]=user1;
				return 1;
			}
		}
		return 0;
	}

	static boolean boardCheck()
	{
		int i,sum=0;
		for(i=0;i<size;i++)
		{
			if(board[0][i]==0)
			{
				sum=1;
				busyrows[i]=0;
			}
			else
			{
				busyrows[i]=1;
			}
		}
		if(sum==1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	static void invincibleFun(int turn,int level)
	{
		int i,j = 0,success;
		for(i=0;i<size && boardCheck();i++)
		{
			if(turn==0 && busyrows[i]==0)  // machines entry
			{
				entryToBoardMachine(i);
				if(checkWin(user1,0,0))
				{
					invincible[i]++;
					for(j=size-1;j>=0;j--)
					{
						if(board[i][j]==0)
						{
							break;
						}
					}
					System.out.println(j+1+" "+level);
					board[i][j+1]=0;
					return ;
				}	
				invincibleFun(1,level+1);
				for(j=size-1;j>=0;j--)
				{
					if(board[i][j]==0)
					{
						break;
					}
				}
				board[i][j+1]=0;
			}
			if(turn==1 && busyrows[i]==0)
			{
				entryToBoardUser(i);
				if(checkWin(8-user1,0,0))
				{
					invincible[i]--;
					for(j=size-1;j>=0;j--)
					{
						if(board[i][j]==0)
						{
							break;
						}
					}
					board[i][j+1]=0;
					return ;
				}	
				invincibleFun(0,level+1);
				for(j=size-1;j>=0;j--)
				{
					if(board[i][j]==0)
					{
						break;
					}
				}
				board[i][j+1]=0;				
			}
				
		}
	}
	static void controller(int user1,int difficulty,int fri_mac)         // machine entries !
	{
		int i=0,success=0,success2=0,surrender_count=0;
		Random random = new Random();
		surrender_count=0;
		int col=0;
		if(difficulty==0)
		{
			while(success==0)
			{
				col = random.nextInt(size);
				success=entryToBoardMachine(col);
			}
			//System.out.println("In here controller successful col is "+col);
			p.println(col);
			return ;
		}
		else
		{
			if(difficulty==1)
			{
				// take win !
				// check if you are loosing  ! 
				// 	if yes
				//		block it
				// 	else
				//		play anything that wont make you loose in next turn !
				// if we pass 1 to check win then 
				if(checkWin(user1,1,0))
				{
					System.out.println("Check 1");
					return;
				}
				else
				{
					if(checkWin(8-user1,1,0))
					{
						System.out.println("Check 2");
						return;
					}
					else
					{
						System.out.println("Check 3");
						while(success==0)
						{
							if(surrender_count==100)
							{
								p.println(size+1);
								surrender=size+1;
								return;
							}
							success2=0;
							while(success2==0)
							{
								col = random.nextInt(size);
								if(busyrows[col]==1)   // if col is busy column then no need to go via procedure !
								{
									continue;
								}
								success2=entryToBoardMachine(col);
							}
							System.out.println(col);
							show();
							if(!checkWin(8-user1,0,1))  // activation_flag is now one we want that code to execute
							{// to check if play is safe for next turn !
								success=1;
								p.println(col);
							}
							else
							{    // if client wins after inserting ball then revert change and find 
								 // other column to insert !
								for(i=size-1;i>=0;i--)
								{
									if(board[i][col]==0)
									{
										break;
									}
								}
								board[i+1][col]=0;
							}
							surrender_count+=1;
							System.out.println("surrender count is "+surrender_count);
						}
					}
					
				}
				
			}
		}
		
	}	
	static void show()
	{
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
	
		
	static boolean checkWin(int user ,int fri_mac,int activation_flag)
	{// i times j is firest element of horizontal check box and k goes way back !
		int i,j,k,l,i_check,j_check,sum_row_wise=0,sum_col_wise=0,four,sum_diagonal_one=0,sum_diagonal_two=0; // a goes from k to k-4
		for(i=size-1;i>=0;i--) // controls rows
		{
			for(j=size-1;j>=3;j--) // controls starting sum.
			{
				l=0;
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
				if(activation_flag==1)//specially used in case 3 for checking if thread is live its not ok
				{	// and machine can loose match directly in next turn !
					if(sum_col_wise==3*user)
					{
						return true;
					}
					if(sum_row_wise==3*user)
					{
						
						k=j;
						four=4;
						while(four>0)
						{
							if(board[i][k]==0)
							{
								if(i+1<size && board[i+1][k]!=0) // check if there exists any element below !
								{
									System.out.println("row wise threat dont insert  !"+i+k);
									return true;
								}
							}
							four--;
							k--;
						}
					}
				}
				
				if(fri_mac==1)    // if you are playing with machine with difficulty hard !
				{// and machine wants to take win !
					if(sum_col_wise==3*user)
					{
						System.out.println("column wise check ! attack/defence ");
						entryToBoardMachine(i);        // entry will be on top obviously !
						/*if(user==user1)
						{*/
							p.println(i);
						//}
						return true ;
					}
					if(sum_row_wise==3*user)
					{
						k=j;
						four=4;
						while(four>0)
						{
							if(board[i][k]==0)
							{
								if(i+1<size && board[i+1][k]==0 ) // check if there exists any element below !
								{
									System.out.println("row wise threat but its ok !"+i+k);
									break;
								}
								else
								{
									entryToBoardMachine(k);
									System.out.println("row wise check ! attack/defence ");
									/*if(user==user1)
									{*/
									p.println(k);
									//}
									return true;
								}
							}
							four--;
							k--;
						}
					}
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
					System.out.println("due to diagonal1 check !");
					return true;
				}
				if(activation_flag==1)
				{
					if(sum_diagonal_one==3*user)
					{
						
						l=i;
						k=j;
						four=4;
					
						while(four>0)
						{
							if(board[l][k]==0)
							{
								if(l+1<size && board[l+1][k]!=0) // check if there exists any element below !
								{
									System.out.println("diagonal1 wise threat dont insert !"+l+k);
									return true;
								}
							}
							l--;
							k--;
							four--;
						}
					}
				}
				if(fri_mac==1)// hard machine diagonal 1 win takes if possible via diagonal 1 check !
				{
					if(sum_diagonal_one==3*user)
					{
						l=i;
						k=j;
						four=4;
					
						while(four>0)
						{
							if(board[l][k]==0)
							{
								if(1+1<size && board[l+1][k]==0 ) // check if there exists any element below !
								{
									System.out.println("diagonal1 wise threat but its ok !"+l+k);
									break;
								}
								entryToBoardMachine(k);
								System.out.println("diagoanl 1 wise check ! attack/defence ");
								/*if(user==user1)
								{*/
									p.println(k);
								//}
								return true;
							}
							l--;
							k--;
							four--;
						}
					}	
				}
			}
		}
		// diagonal two check !
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
				if(activation_flag==1)
				{
					if(sum_diagonal_two==3*user)
					{
						
						l=i;
						k=j;
						four=4;
						while(four>0)
						{
							if(board[l][k]==0)
							{
								if(1+1<size && board[l+1][k]!=0) // check if there exists any element below !
								{
									System.out.println("diagonal2 wise threat dont insert !" +l+k);
									return true;
								}
							}
							l--;
							k++;
							four--;
						}
					}
				}
				if(fri_mac==1)// machine takes win if possible on hard level via diagonal 2 check!
				{
					if(sum_diagonal_two==3*user)
					{
						l=i;
						k=j;
						four=4;
						while(four>0)
						{
							if(board[l][k]==0)
							{
								if(1+1<size && board[l+1][k]==0) // check if there exists any element below !
								{
									System.out.println("diagonal2 wise threat but its ok !" +l+k);
									break;
								}
								entryToBoardMachine(k);
								System.out.println("diagonal 2 wise check ! attack/defence ");
								/*if(user==user1)
								{*/
								p.println(k);
								//}
								return true;
							}
							l--;
							k++;
							four--;
						}
					}
				}
			}
		}
		return false;
	}
		
	
		
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try(ServerSocket serverSocket=new ServerSocket(5000))
		{
			System.out.println("waiting for client ........");
			Socket socket=serverSocket.accept();
			System.out.println("client connected !");
			BufferedReader B= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			p=new PrintWriter(socket.getOutputStream(),true);
			
			Scanner sc=new Scanner(System.in);
			int difficulty=0,turn,user2,column=0,fri_mac,j,i;
			surrender=0;
			
			turn=Integer.parseInt(B.readLine());
			user2=Integer.parseInt(B.readLine());
			user1=Integer.parseInt(B.readLine());
			size=Integer.parseInt(B.readLine());
			fri_mac=Integer.parseInt(B.readLine());
			if(fri_mac==1)
			{
				difficulty=Integer.parseInt(B.readLine());
				if(difficulty==2)// initializing invincible !
				{
					invincible=new int[size];
				}
			}
			System.out.println("fri_mac is "+fri_mac+" difficulty is "+difficulty);
			busyrows=new int[size];
			if(fri_mac==0)
			{
				if(user1==3)
				{
					System.out.println("you are red (X)");
					System.out.println("so client is yellow (Y)");
				}
				else
				{
					System.out.println("you are yellow (y)");
					System.out.println("so client is red (x)");
				}
			}
			System.out.println("Size of board is "+size);
			board=new int[size][size];
			int success;
			int win_status;
			System.out.println("value of column starts from 0 to 'size -1' ");
			System.out.println("so be careful !");
			while(boardCheck())
			{
				success=0;
				show();
				if(turn==0)
				{
					
					if(fri_mac==0)  // as a human friend !
					{
						while(success==0)
						{
							System.out.println("Server enter column in which you wanna insert ball ");
							column=sc.nextInt();
							if(column < size && column >=0)
							{
								success=entryToBoardMachine(column);
							}		
							else
							{
								System.out.println(" invalid I/P or row should be full ");
							}
							
						}
						p.println(column);
					}
					else       // as a machine !
					{
						System.out.println("IN here !");
						if(difficulty==0 || difficulty==1)
						{
							controller(user1,difficulty,fri_mac);
						}
						else
						{	
							if(difficulty==2)
							{
								invincibleFun(0,0);
								j=0;
								for(i=1;i<size;i++)
								{
									if(invincible[i]>invincible[j])
									{
										j=i;
									}
								}
								entryToBoardMachine(j);
								p.println(j);
							}
						}
					}
					
					turn=1;
					
					if(checkWin(user1,0,0))  // check win is kind of flag 
					{
						show();
						System.out.println("Congo ! server you win !");
						System.exit(0);
					}
				}	
				else
				{
					if(turn==1)
					{	
						System.out.println("Wait its clients turn !");
						if(surrender!=size+1)
						{
							column=Integer.parseInt(B.readLine());
							//entryToBoard(column, user2);
							//
							for(i=size-1;i>=0;i--)
							{
								if(board[i][column]==0)
								{
									board[i][column]=user2;
									break;
								}	
							}	
						//
						}
						if(checkWin(user2,0,0) || surrender==size+1)
						{
							show();
							System.out.println("Sorry server ! You lost game !");
							System.exit(0);
						}
						turn=0;
					}	
				}	
			}
		
		System.out.println("Nothing to play match darws!");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
