#include <Math.hpp>
#include "Algorithms.h"
#include <math.h>

//------------------------------------------------------
void DetectMoleculesFromScratch(FrameSeq& Movie,PPars& Pars,MolecList *MList)
{
   Frame& Fr=*Movie.GetCurFrame();
   Frame Marks(Fr.GetXDim(),Fr.GetYDim());
   MolecList *MaxList=new MolecList(10000),*CurList=new MolecList(500);
   DetectMolsAndMaxs(Fr,Marks,MaxList,Pars,MList);
   int x,y,i,k;
   bool AlreadyOnTheList;
//   fstream file;
//   file.open("log.txt",ios::out);
//   file<<"Starting molecule list:"<<endl;
//   for(i=0;i<MList->Count;i++) file<<MList->Mol(i)<<endl;
   for(int j=1;j<Pars.NofStartFrames;j++)
   {
//      file<<endl<<"Frame "<<j+1<<endl;
      MaxList->Clear(); Marks.Reset();
      Movie.LoadFrame(Movie.GetFrameNumber()+1);
      DetectMolsAndMaxs(Fr,Marks,MaxList,Pars,CurList);
      for(i=MList->Count-1;i>=0;i--)
      {
         x=MList->Mol(i).x; y=MList->Mol(i).y;
         Fr.ShiftToLocalMax(x,y);
         if( Hypot(x-MList->Mol(i).x,y-MList->Mol(i).y)<=Pars.ImRad
             && CheckIfMolecule(Fr,x,y,Pars) )
         {                              //the same molecule and still visible
            MList->Mol(i).x=x; MList->Mol(i).y=y; MList->Mol(i).I++;
//            file<<"Kept (detected):"<<MList->Mol(i)<<endl;
         }else if(MList->Mol(i).I<1)
//               {
//                  file<<"Deleted:\t"<<MList->Mol(i)<<endl;
                  MList->Delete(i);
//               }else file<<"Kept:\t\t"<<MList->Mol(i)<<endl;
      }
      for(i=0;i<CurList->Count;i++)
      {
         AlreadyOnTheList=false;
         for(k=0;k<MList->Count;k++)
            if(Dist(CurList->Mol(i),MList->Mol(k))<=Pars.ImRad) AlreadyOnTheList=true;
         if(!AlreadyOnTheList)
         {
            MList->Add(CurList->Mol(i));
//            file<<"Added:\t\t"<<CurList->Mol(i)<<endl;
         }
      }
      CurList->Clear();
   }
   for(i=MList->Count-1;i>=0;i--) if(MList->Mol(i).I<1) MList->Delete(i);
   MarkBrightNonMolecules(Fr,Marks,MaxList,Pars);
   CalcSignals(Fr,Marks,MList,Pars,true);
   delete MaxList;  delete CurList;
//   file.close();
}
//------------------------------------------------------
Frame GetMarksFrame(Frame& Fr,PPars& Pars)
{
   Frame Marks(Fr.GetXDim(),Fr.GetYDim());
   MolecList *MaxList=new MolecList(10000), *MList=new MolecList(500);
   DetectMolsAndMaxs(Fr,Marks,MaxList,Pars,MList);
   MarkBrightNonMolecules(Fr,Marks,MaxList,Pars);
   delete MaxList; delete MList;
   return Marks;
}
//------------------------------------------------------
void FollowMolecules(Frame& Fr,MolecList* RefList,PPars& Pars,MolecList* CurList)
{
   Frame Marks(Fr.GetXDim(),Fr.GetYDim());
   MolecList *MaxList=new MolecList(10000);
   MolecList *MList=new MolecList(500);
   DetectMolsAndMaxs(Fr,Marks,MaxList,Pars,MList);
   MarkBrightNonMolecules(Fr,Marks,MaxList,Pars);
   int i,x,y;
   for(i=0;i<RefList->Count;i++)
   {
      x=RefList->Mol(i).x; y=RefList->Mol(i).y;
      Fr.ShiftToLocalMax(x,y);
      if( Hypot(x-RefList->Mol(i).x,y-RefList->Mol(i).y)<=Pars.ImRad
          && CheckIfMolecule(Fr,x,y,Pars) )
      {                              //the same molecule and still visible
         CurList->Add(Molecule(x,y,0));
         RefList->Mol(i).x=x; RefList->Mol(i).y=y;
      }else CurList->Add(Molecule(RefList->Mol(i).x,RefList->Mol(i).y,0));
   }
   CalcSignals(Fr,Marks,CurList,Pars,false);
   delete MList;
   delete MaxList;
}
//------------------------------------------------------
void DetectMolsAndMaxs(Frame& Fr,Frame& Marks,MolecList* MaxList,PPars& Pars,MolecList* MList)
{
   float MolSum,dist;
   int i,j,i0,j0,XDim=Fr.GetXDim(),YDim=Fr.GetYDim();
   int istart=Max((float)Pars.BrightNum-1,Pars.SmRad),jstart=istart;
   int iend=Min((float)XDim-Pars.BrightNum,XDim-Pars.SmRad-1);
   int jend=Min((float)YDim-Pars.BrightNum,YDim-Pars.SmRad-1);
   if(Pars.UseROI)
   {
      istart=Max(istart,Pars.roiLeft);
      jstart=Max(jstart,Pars.roiTop);
      iend=Min(iend,Pars.roiRight);
      jend=Min(jend,Pars.roiBottom);
   }
   for(i=istart;i<=iend;i++) for(j=jstart;j<=jend;j++)
   {
      if( Fr.CheckIfLocalMax(i,j) && !Marks[j][i] && (!Pars.UseROI || CheckIfInROI(i,j,Pars)) &&
         (!Pars.UseExProfile || (*Pars.ExFrame)[j][i]>Pars.CutOff*Pars.ExFrame->GetMax())  )
      {
         if(CheckIfMolecule(Fr,i,j,Pars) )
         {
            for(i0=i-Pars.ImRad;i0<=i+Pars.ImRad;i0++) for(j0=j-Pars.ImRad;j0<=j+Pars.ImRad;j0++)
               if(Hypot(j0-j,i0-i)<=Pars.ImRad) Marks[j0][i0]++;//i.e. at least one molecule is here
            MList->Add(Molecule(i,j,0));
         }else MaxList->Add(Molecule(i,j,0));
      }
   }
   MaxList->Capacity=MaxList->Count+MList->Count;
   MList->Capacity=MList->Count;
}
//------------------------------------------------------
void MarkBrightNonMolecules(Frame& Fr,Frame& Marks,MolecList* MaxList,PPars& Pars)
{
   Statistics Stats;
   int x,y,i,j;
   for(int m=0;m<MaxList->Count;m++)
   {
      x=MaxList->Mol(m).x; y=MaxList->Mol(m).y;
      if( Marks[y][x]==0 && Fr.CheckIfLocalMax(x,y) )
      {
         Stats=CalcLocStat(Fr,Marks,x,y,Pars);
         if(Fr[y][x]>Stats.Mean+Pars.NoiseSigms*Stats.StDev)
         {
            Marks[y][x]++;
            for(i=x-Pars.SmRad;i<=x+Pars.SmRad;i++) for(j=y-Pars.SmRad;j<=y+Pars.SmRad;j++)
            {
               if(!Marks[j][i] && Fr[j][i]>Stats.Mean+Pars.NoiseSigms*Stats.StDev &&
                   Hypot(j-y,i-x)<=Pars.SmRad ) Marks[j][i]++;
            }
         }
      }
   }
}
//------------------------------------------------------
void CalcSignals(Frame& Fr,Frame& Marks, MolecList* MList,PPars& Pars,bool filter)
{
   int n,x,y,num,i,j;
   float MolSum;
   Statistics Stats;
   for(n=0;n<MList->Count;n++)
   {
      x=MList->Mol(n).x; y=MList->Mol(n).y;
      Stats=CalcLocStat(Fr,Marks,x,y,Pars);
      if(filter && Fr[y][x]<Stats.Mean+Pars.NoiseSigms*Stats.StDev)
      {
         MList->Delete(n);
         n--;
      }else
      {
         MolSum=0;num=0;
         for(i=x-Pars.ImRad;i<=x+Pars.ImRad;i++)
            for(j=y-Pars.ImRad;j<=y+Pars.ImRad;j++)
               if(Hypot(i-x,j-y)<=Pars.ImRad) {MolSum+=Fr[j][i];num++;};
         MList->Mol(n).I=MolSum-Stats.Mean*num;
      }
   }
   MList->Capacity=MList->Count;
}
//------------------------------------------------------
bool CheckIfMolecule(Frame& Fr,int i,int j,PPars& Pars)
{
   MolecList* BrightList=new MolecList(Pars.BrightNum); //here this is a list of pixels, not molecules
   BrightList->Add(Molecule(i,j,Fr[j][i]));
   Fr[j][i]*=-1; //initializing the "bright list" and marking the pixel #0
   int k;
   for(k=1;k<=Pars.BrightNum-1;k++) AddNextBrightest(Fr,BrightList);
   bool molecule=true;
   for(k=0;k<Pars.BrightNum;k++)// cannot optimize the loop with "&& molecule" here!
   {
      Fr[BrightList->Mol(k).y][BrightList->Mol(k).x]*=-1; //unmarking the "bright list"
      if ( Dist(BrightList->Mol(0),BrightList->Mol(k)) > Pars.BrightSize ) molecule=false;
   }
   if(molecule)
   {     //if molecule==true, additional criterion needs to be checked
      float dist,I,distaver=0,Iaver=0,dist2aver=0,I2aver=0,distIaver=0;
      k=0;
      for(int i0=-Pars.ImRad;i0<=Pars.ImRad;i0++) for(int j0=-Pars.ImRad;j0<=Pars.ImRad;j0++)
      {
         dist=Hypot(i0,j0);
         if(dist<=Pars.ImRad)
         {
            I=Fr[j+j0][i+i0];
            distaver+=dist;
            dist2aver+=dist*dist;
            Iaver+=I;
            I2aver+=I*I;
            distIaver+=dist*I;
            k++;
         }
      }
      float PearsonR=(distIaver-distaver*Iaver/k)/
            sqrt( (dist2aver-distaver*distaver/k)*(I2aver-Iaver*Iaver/k) );
      if(PearsonR>-Pars.Correlation) molecule=false;
   }
   delete BrightList;
   return molecule;
}
//------------------------------------------------------
bool CheckIfInROI(int i,int j,PPars& Pars)
{
   float xc=0.5*(Pars.roiLeft+Pars.roiRight);
   float yc=0.5*(Pars.roiTop+Pars.roiBottom);
   float ra=0.5*(Pars.roiRight-Pars.roiLeft);
   float rb=0.5*(Pars.roiBottom-Pars.roiTop);
   return (IntPower((xc-i)/ra,2)+IntPower((yc-j)/rb,2))<1;
}
//------------------------------------------------------
Statistics CalcLocStat(Frame &Fr,Frame& Marks,int ic,int jc,PPars& Pars)
{  //mean and stdev among unoccupied pixels in a square (side is 2*SmRad)
   int i,j,num=0;
   Statistics Stats;
   for(i=ic-Pars.SmRad;i<=ic+Pars.SmRad;i++) for(j=jc-Pars.SmRad;j<=jc+Pars.SmRad;j++)
   {
      if(!Marks[j][i])
      {
         Stats.Mean+=Fr[j][i];
         Stats.StDev+=IntPower(Fr[j][i],2);
         num++;
      }
   }
   if(!num) return Stats;
   Stats.Mean/=num;
   Stats.StDev=sqrt(Stats.StDev/num-Stats.Mean*Stats.Mean);
   return Stats;
}
//------------------------------------------------------
void AddNextBrightest(Frame &Fr,MolecList *BrightList)
{
   int x,y,i,j,m,N=BrightList->Count;
   Molecule max(0,0,0);
   for(m=0;m<N;m++)
   {
      x=BrightList->Mol(m).x; y=BrightList->Mol(m).y;
      for(i=x-1;i<=x+1;i++) for(j=y-1;j<=y+1;j++)
         if(Fr[j][i]>=max.I) max=Molecule(i,j,Fr[j][i]);
   }
   Fr[max.y][max.x]*=-1;//marking the pixel as already put into the "bright list"
   BrightList->Add(max);
}
//------------------------------------------------------
void AddSignalValues(Frame& Fr,float *ExSignals,float *EmSignals,PPars &Pars,int f)
{
   int Left=Pars.roiLeft,Right=Pars.roiRight,Top=Pars.roiTop,Bottom=Pars.roiBottom;
   Pars.roiLeft=Pars.ExRoiLeft; Pars.roiRight=Pars.ExRoiRight;
   Pars.roiTop=Pars.ExRoiTop; Pars.roiBottom=Pars.ExRoiBottom;
   float sum=0; int i,j;
   for(i=Pars.roiLeft;i<=Pars.roiRight;i++) for(j=Pars.roiTop;j<=Pars.roiBottom;j++)
   {
      if(CheckIfInROI(i,j,Pars)) sum+=Fr[j][i];
   }
   ExSignals[f]=sum; sum=0;
   Pars.roiLeft=Pars.EmRoiLeft; Pars.roiRight=Pars.EmRoiRight;
   Pars.roiTop=Pars.EmRoiTop; Pars.roiBottom=Pars.EmRoiBottom;
   for(i=Pars.roiLeft;i<=Pars.roiRight;i++) for(j=Pars.roiTop;j<=Pars.roiBottom;j++)
   {
      if(CheckIfInROI(i,j,Pars)) sum+=Fr[j][i];
   }
   EmSignals[f]=sum;
   Pars.roiLeft=Left;Pars.roiRight=Right;Pars.roiTop=Top;Pars.roiBottom=Bottom;
}
//------------------------------------------------------
void ImproveSignals(float* Sig,int N)
{
   float StepSqAver=0, StepAver=0, Step;
   int i,j;
   for(i=0;i<N-1;i++)
   {
      Step=fabs(Sig[i+1]-Sig[i]);
      StepAver+=Step;
      StepSqAver+=Step*Step;
   }
   StepSqAver/=N-1; StepAver/=N-1;
   float StepStD=sqrt(StepSqAver-StepAver*StepAver);
   int *starts=new int[N/2],*stops=new int[N/2];
   int nstarts=0,nstops=0;
   for(i=0;i<N-1;i++)
   {
      if(Sig[i+1]-Sig[i]>StepAver+2.5*StepStD)
      {
          for(j=i;Sig[j+1]>Sig[j] && j>=0;j--){};
          nstarts++; starts[nstarts-1]=(j+1)*(j>=0);
          for(;Sig[i+1]>Sig[i] && i<N-1;i++){};
      }
      if(Sig[i]-Sig[i+1]>StepAver+2.5*StepStD)
      {
          for(j=i;Sig[j+1]<Sig[j] && j<N-1;j++){};
          if(j<N-1) {nstops++;stops[nstops-1]=j; }
          i=j;
      }
      if(nstops==nstarts-1) stops[nstarts-1]=N-1;
   }
   j=0; float subtr;
   for(i=0;i<nstarts;i++)
   {
       for(;j<starts[i];j++){Sig[j]=0;};
       subtr=Min(Sig[starts[i]],Sig[stops[i]]);
       for(;j<=stops[i];j++){Sig[j]-=subtr;}
   }
   for(;j<N;j++){Sig[j]=0;};
   delete[] starts; delete[] stops;
}
//------------------------------------------------------

