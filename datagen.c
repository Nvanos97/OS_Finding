/*
Simple program for random number data set generation. Written by Bojian Xu. 
Only for convinience, so don't try to abuse this code in order to make it crash :-)


Usage: Program_name [number of of random integers (in 1000,1000)]

For example: Say the executable file is named as "a.out", then: 

command "a.out 10" will generate a file named "data.txt" that has 10,000,000 random integers. 
command "a.out 100" will generate a file named "data.txt" that has 100,000,000 random integers. 
...

Feel free to change the source code below if you know a bit about C programming. 

*/


#include<stdio.h>
#include<stdlib.h>
#include<string.h>

int main(int argc, char **argv)
{
  long int size;
  long int i; 
  FILE *fp; 


  size = atoi(argv[1]);
  size *= 1000;
  size *= 1000;

  fp = (FILE *)fopen("data.txt", "w");
  if(fp == NULL){
    fprintf(stderr, "data file open failed. exit\n");
    exit(1);
  }

  for(i = 0; i < size; i++)
    fprintf(fp, "%d\n", rand());

  fclose(fp);

  return 0;
}

