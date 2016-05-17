package com.ilabs.utterance;


import java.util.*;





public class test  {
	
	public static void run(String val)
	{
		System.out.println(val);
	}
}
//	int s;
//	int f;
//	static List<test> l = new ArrayList<test>();
//	test(int s,int f)
//	{
//		this.s = s;
//		this.f = f;
//		
//	}
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		
////		l.add(new test(1,3));
////		l.add(new test(5,7));
////		l.add(new test(2,4));
////		l.add(new test(6,8));
////		Collections.sort(l,new Comp());
////		
////		for(int i= 0;i<4;i++)
////		System.out.println(l.get(i).s +" " +l.get(i).f);
//		
////		Scanner sc = new Scanner(System.in);
////		int n = sc.nextInt();
////		int ar[] = new int[n];
////		for(int i=0;i<n;i++)
////		{
////			ar[i] = sc.nextInt();
////		}
////		
////		int max = ar[n-1];
////		LinkedList<Integer> l = new LinkedList<Integer>();
////		l.add(max);
////		for(int i = n-2;i>=0;i--)
////		{
////			if(ar[i]>max)
////			{
////				l.addFirst(ar[i]);
////				max = ar[i];
////			}
////			else l.addFirst(max);
////		}
////		
////		System.out.println(l);
////		String[] s = new String[n];
////		int i = 0;
////		while(i<n)
////		{
////			s[i] = sc.nextLine(); i++;
////		}
////		
////		// for first two
////		if(n==1)
////			System.out.println(s[0]);
////		else{
////			int p =0;
////			int q= 0;
////			
////			while(s[0].charAt(p)==s[1].charAt(q))
////			{
////				p++;
////				q++;
////			}
////			String pref = s[0].substring(0, p+1);
////			
////			for(i=2;i<n;i++)
////			{
////				if(!pref.equals(s[i].substring(0, p+1)))
////				{
////					q=0;
////					int t = 0;
////					while(pref.charAt(q)== s[i].charAt(t))
////					{
////						q++;t++;
////					}
////						
////				}
////				pref = s[i].substring(0, q);
////			}
////		}
////		int x = 9;
////		int y = 1;
////		int n1 = x^y;
////		System.out.println(n1);
////		int count =0;
////		while (n1>0)
////	    {
////	      n1 &= (n1-1) ;
////	      count++;
////	    }
////		System.out.println(count);
////		if(count==1)System.out.println("Yes");
////		else System.out.println("No");
////		  Scanner sc = new Scanner(System.in);
////		  //numbers in the form of linkedlist
////		  LinkedList<Integer> n1 = new LinkedList<Integer>();
////		  LinkedList<Integer> n2 = new LinkedList<Integer>();
////		  LinkedList<Integer> n3 = new LinkedList<Integer>();
////		  
////		  int s;
////		  
////		  s = sc.nextInt();
////		  int i = 0;
////		  while(i<s)
////			  {
////			  	n1.add(sc.nextInt());
////			  	i++;
////			  }
////		  s = sc.nextInt();
////		  i=0;
////		  while(i<s)
////		  {
////			  n2.add(sc.nextInt());
////			  i++;
////		  }
////		  s = sc.nextInt();
////		  i=0;
////		  while(i<s)
////		  {
////			  n3.add(sc.nextInt());
////			  i++;
////		  }
////		  
////		//  System.out.println(n1+" "+n2+" "+n3);
////		  
////		  int l = Math.max(n1.size(), Math.max(n2.size(),n3.size()));
////		  System.out.println(l);
////		  
////		  LinkedList<Integer> sum = new LinkedList<Integer>();
////		  int carry = 0;
////		  while(!n1.isEmpty() && ! n2.isEmpty() && !n3.isEmpty())
////		  {
////			  int s1 = carry + n1.getLast()+n2.getLast()+n3.getLast();
////			  carry = s1/10;
////			  sum.addFirst(s1%10);
////			  n1.removeLast();
////			  n2.removeLast();
////			  n3.removeLast();
////		  }
////		  
////		  if(n1.isEmpty())
////		  {
////			  while(! n2.isEmpty() && !n3.isEmpty())
////			  {
////				  int s1 = carry +n2.getLast()+n3.getLast();
////				  carry = s1/10;
////				  sum.addFirst(s1%10);
////				  n2.removeLast();
////				  n3.removeLast();
////			  }
////			  
////			  if(n2.isEmpty())
////			  {
////				  while( !n3.isEmpty())
////				  {
////					  int s1 = carry +n3.getLast();
////					  carry = s1/10;
////					  sum.addFirst(s1%10);
////					  n3.removeLast();
////				  }
////			  }
////			  else if(n3.isEmpty())
////			  {
////				  while(! n2.isEmpty())
////				  {
////					  int s1 = carry +n2.getLast();
////					  carry = s1/10;
////					  sum.addFirst(s1%10);
////					  n2.removeLast();
////				  }
////			  }
////		  }
////		  
////		  
////		  if(n2.isEmpty())
////		  {
////			  while(! n1.isEmpty() && !n3.isEmpty())
////			  {
////				  int s1 = carry +n1.getLast()+n3.getLast();
////				  carry = s1/10;
////				  sum.addFirst(s1%10);
////				  n1.removeLast();
////				  n3.removeLast();
////			  }
////			  
////			  if(n1.isEmpty())
////			  {
////				  while( !n3.isEmpty())
////				  {
////					  int s1 = carry +n3.getLast();
////					  carry = s1/10;
////					  sum.addFirst(s1%10);
////					  n3.removeLast();
////				  }
////			  }
////			  else if(n3.isEmpty())
////			  {
////				  while(! n1.isEmpty())
////				  {
////					  int s1 = carry +n1.getLast();
////					  carry = s1/10;
////					  sum.addFirst(s1%10);
////					  n1.removeLast();
////				  }
////			  }
////		  }
////		  
////		  if(n3.isEmpty())
////		  {
////			  while(! n2.isEmpty() && !n1.isEmpty())
////			  {
////				  int s1 = carry +n2.getLast()+n1.getLast();
////				  carry = s1/10;
////				  sum.addFirst(s1%10);
////				  n2.removeLast();
////				  n1.removeLast();
////			  }
////			  
////			  if(n2.isEmpty())
////			  {
////				  while( !n1.isEmpty())
////				  {
////					  int s1 = carry +n1.getLast();
////					  carry = s1/10;
////					  sum.addFirst(s1%10);
////					  n1.removeLast();
////				  }
////			  }
////			  else if(n1.isEmpty())
////			  {
////				  while(! n2.isEmpty())
////				  {
////					  int s1 = carry +n2.getLast();
////					  carry = s1/10;
////					  sum.addFirst(s1%10);
////					  n2.removeLast();
////				  }
////			  }
////		  }
////		  if(carry != 0)
////			  sum.addFirst(carry);
////		  System.out.println(sum);
//		  
//		}
////	static class Comp implements Comparator<test>{
////
////		@Override
////		public int compare(test t1, test t2) {
////			// TODO Auto-generated method stub
////			//System.out.println(t1.s+" "+t2.f);
////			int x = t1.compareTo(t2);
////			//System.out.println(x);
////			if( x != 1)
////			{
////				//System.out.println(t1.s+" "+t2.s);
////				test temp = t1;
////				t1 = t2;
////				t2 =temp;
////				//System.out.println(t1.s+" "+t2.s);
////				
////			}
////			for(int i= 0;i<4;i++)
////				System.out.println(l.get(i).s +" " +l.get(i).f);
////			System.out.print("\n--------\n");
////			return 0;
////		}
////		
////	}
////	@Override
////	public int compareTo(test t) {
////		// TODO Auto-generated method stub
////		
////		//System.out.println(this.s+" "+t.f);
////		return this.s > t.s ? 1:0;
////	}
//}
