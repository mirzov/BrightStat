#include "FrameSeq.h"
#include <Math.hpp>
#include <math.h>
//#include <Forms.hpp>
#include <mem.h>
//------------------------------------------------------
Frame::Frame()
{
   ImageData=new int[1];
   ImageData[0]=0;
   XDim=YDim=1;
   MaxValue=MeanValue=MinValue=0;
};
//------------------------------------------------------
Frame::Frame(int i,int j)
{
   if( (i>0)&&(j>0)&&(i<MaxDim)&&(j<MaxDim) )
   {
      ImageData=new int[i*j];
      setmem(ImageData,i*j*sizeof(int),0);
      XDim=i;
      YDim=j;
      MaxValue=MeanValue=MinValue=0;
      return;
   };
   Frame();
};
//------------------------------------------------------
Frame::Frame(const Frame &F)
{
   XDim=F.XDim;
   YDim=F.YDim;
   ImageData=new int[XDim*YDim];
   memcpy(ImageData,F.ImageData,XDim*YDim*sizeof(int));
   MaxValue=F.MaxValue;
   MinValue=F.MinValue;
   MeanValue=F.MeanValue;
};
//------------------------------------------------------
void Frame::Reset()
{
   setmem(ImageData,XDim*YDim*sizeof(int),0);
   MaxValue=MeanValue=MinValue=0;
};
//------------------------------------------------------
Frame& Frame::operator=(const Frame& F)
{
   delete[] ImageData;
   XDim=F.XDim;
   YDim=F.YDim;
   ImageData=new int[XDim*YDim];
   memcpy(ImageData,F.ImageData,XDim*YDim*sizeof(int));
   MaxValue=F.MaxValue;
   MeanValue=F.MeanValue;
   MinValue=F.MinValue;
   return *this;
}
//------------------------------------------------------
Frame& Frame::operator-=(const Frame& F)
{
   if(XDim!=F.XDim || YDim!=F.YDim) return *this;
   MinValue=1e25,MaxValue=-1e25;
   int temp,sum=0;
   for(int i=0;i<YDim*XDim;i++)
   {
      temp=ImageData[i]-=F.ImageData[i];
      if(temp<MinValue) MinValue=temp;
      if(temp>MaxValue) MaxValue=temp;
      sum+=temp;
   }
   MeanValue=sum/XDim/YDim;
   return *this;
}
//------------------------------------------------------
Frame::~Frame()
{
   delete[] ImageData;
};
//------------------------------------------------------
int* Frame::operator[](int j)
{
   if(j<0) j=0;
   if(j>=YDim) j=YDim-1;
   return ImageData+j*XDim;
}
//------------------------------------------------------
void Frame::DrawCanvas(TCanvas *Canvas,DrawParams& pars)
{   //any ROI parameters can be used, because only the overlap between the given ROI
   int XShift=pars.Rect.Left<0?-pars.Rect.Left:0; //and the Frame data is used
   int YShift=pars.Rect.Top<0?-pars.Rect.Top:0;
   Canvas->Brush->Color=clBlack;
   Canvas->FillRect(pars.Rect);
   int color; //clearing the canvas
   int bitshift=8*pars.Color;
   TColor PixColor;
   float temp;
   if (pars.MaxSignal == 0) return;
   for(int i=pars.Rect.Top+YShift;i<=Min(pars.Rect.Bottom,YDim-1);i++)
   {
      for(int j=pars.Rect.Left+XShift;j<=Min(pars.Rect.Right,XDim-1);j++)
      {
         temp=(float)(ImageData[j+i*XDim]-MinValue)/pars.MaxSignal;
         color=Log2(temp*15+1)/4*255;
         PixColor=TColor(color<<bitshift);
         Canvas->Pixels[j-pars.Rect.Left][i-pars.Rect.Top]=PixColor;
      }
   }
}
//------------------------------------------------------
void Frame::CalcParams()
{
   MinValue=1e25,MaxValue=-1e25;
   int temp,sum=0;
   for(int i=0;i<YDim*XDim;i++)
   {
      temp=*(ImageData+i);
      if(temp<MinValue) MinValue=temp;
      if(temp>MaxValue) MaxValue=temp;
      sum+=temp;
   }
   MeanValue=sum/XDim/YDim;
}
//------------------------------------------------------
bool Frame::CheckIfLocalMax(int i,int j)
{
   if(i<1 || j<1 || i>XDim-2 || j>YDim-2 ) return false;
   for(int i0=-1;i0<=1;i0++) for(int j0=-1;j0<=1;j0++)
      if (ImageData[j*XDim+i]<=ImageData[(j+j0)*XDim+i+i0] && (i0||j0)) return false;
   return true;
}
//------------------------------------------------------
void Frame::ShiftToLocalMax(int &x,int &y)
{
   int i,j,max=ImageData[y*XDim+x],xmax=x,ymax=y;
   bool shift;
   do
   {
      shift=false;
      for(i=x-1;i<=x+1;i++) for(j=y-1;j<=y+1;j++)
      {
         if(ImageData[j*XDim+i]>max)
         {
            max=ImageData[j*XDim+i];
            xmax=i; ymax=j;
            shift=true;
         }
      }
      x=xmax; y=ymax;
   } while(shift);
}
//------------------------------------------------------
int Frame::LoadFromFile(AnsiString filename)
{
   TFileStream* File=new TFileStream(filename,fmOpenRead|fmShareExclusive);
   if(!File) return 1;
   File->Seek(108,soFromBeginning);
   short int datatype;
   File->Read(&datatype,2);
   if(datatype == 0) return 2; //FLOATING POINT intensity format is not supported
   short int DatLength=datatype>1?2:4;
   File->Seek(1446,soFromBeginning);
   int NFramestemp=0,XDimtemp=0,YDimtemp=0;
   File->Read(&NFramestemp,4);
   File->Seek(42,soFromBeginning);
   File->Read(&XDimtemp,2);
   File->Seek(656,soFromBeginning);
   File->Read(&YDimtemp,2);
   if( (NFramestemp<1)||(NFramestemp>MaxFrames)||(!datatype)||(XDimtemp<1)
     ||(XDimtemp>MaxDim)||(YDimtemp<1)||(YDimtemp>MaxDim) )
   {
      delete File;
      return 1;
   }
   *this=Frame(XDimtemp,YDimtemp);
   File->Seek(4100,soFromBeginning);
   if(Load(File,DatLength))
   {
      delete File;
      return 1;
   }
   delete File;
   return 0;
};
//------------------------------------------------------
int Frame::Load(TFileStream* File, short int DatLength)
{
   int temp;
   MinValue=2e9; MaxValue=-2e9;
   float sum=0;
   for(int i=0;i<XDim*YDim;i++)
   {
      temp=0;
      if(File->Read(&temp,DatLength)<DatLength) return 1;
      *(ImageData+i)=temp;
      sum+=temp;
      if(temp>MaxValue) MaxValue=temp;
      if(temp<MinValue) MinValue=temp;
   }
   MeanValue=sum/XDim/YDim;
   return 0;
}
//------------------------------------------------------
int Frame::SaveToFile(AnsiString filename, void* Header)
{
   TFileStream* File=new TFileStream(filename,fmCreate|fmShareExclusive);
   if(!File) return 1;
   File->Seek(0,soFromBeginning);
   File->Write(Header,4100);
   int tmp=3;
   File->Seek(108,soFromBeginning);
   File->Write(&tmp,2);
   tmp=XDim;
   File->Seek(42,soFromBeginning);
   File->Write(&tmp,2);
   tmp=YDim;
   File->Seek(656,soFromBeginning);
   File->Write(&tmp,2);
   tmp=1;
   File->Seek(1446,soFromBeginning);
   File->Write(&tmp,4);
   File->Seek(4100,soFromBeginning);
   for(int i=0;i<XDim*YDim;i++)
   {
      tmp=*(ImageData+i);
      File->Write(&tmp,2);
   }
   delete File;
   return 0;
}
//------------------------------------------------------
FrameSeq::FrameSeq()
{
   NFrames=FrameNumber=XDim=YDim=1;
   File=NULL; Header=NULL;
   CurFrame=new Frame;
}
//------------------------------------------------------
FrameSeq::~FrameSeq()
{
   FreeFile();
}
//------------------------------------------------------
int FrameSeq::Load(AnsiString filename)
{
   FreeFile();
   try {File=new TFileStream(filename,fmOpenRead|fmShareExclusive);}
   catch(...) {return 1;}
   File->Seek(0,soFromBeginning);
   Header=new __int8[4100];
   File->Read(Header,4100);
   File->Seek(108,soFromBeginning);
   short int datatype;
   File->Read(&datatype,2);
   if(datatype == 0) return 2; //FLOATING POINT intensity format is not supported
   DatLength=datatype>1?2:4;
   File->Seek(1446,soFromBeginning);
   int NFramestemp=0,XDimtemp=0,YDimtemp=0;
   File->Read(&NFramestemp,4);
   File->Seek(42,soFromBeginning);
   File->Read(&XDimtemp,2);
   File->Seek(656,soFromBeginning);
   File->Read(&YDimtemp,2);
   if( (NFramestemp<1)||(NFramestemp>MaxFrames)||(!datatype)||(XDimtemp<1)
     ||(XDimtemp>MaxDim)||(YDimtemp<1)||(YDimtemp>MaxDim) )
   {
      delete File; File=NULL;
      return 1;
   }
   NFrames=NFramestemp;
   XDim=XDimtemp; YDim=YDimtemp;
   CurFrame=new Frame(XDim,YDim);
//   LoadFrame(1);
   return 0;
};
//------------------------------------------------------
void FrameSeq::FreeFile()
{
   if(File) delete File;
   File=NULL;
   if(Header) delete[] Header;
   Header=NULL;
   if(CurFrame) delete CurFrame;
   CurFrame=NULL;
}
//------------------------------------------------------
int FrameSeq::LoadFrame(int number)
{
   if(!File) return 1;
   File->Seek(4100+XDim*YDim*DatLength*(number-1),soFromBeginning);
   FrameNumber=number;
   if(CurFrame->Load(File,DatLength))
   {
      delete CurFrame; CurFrame=NULL;
      delete File; File=NULL;
      return 1;
   }
   return 0;
}
//------------------------------------------------------
float FrameCorr(Frame& fr1,Frame& fr2,TRect roi1,TRect roi2)
{
   if(roi1.Height()!=roi2.Height() || roi1.Width()!=roi2.Width() ) return 0;
   int temp1,temp2;
   temp1=roi1.Left<0?-roi1.Left:0;
   temp2=roi2.Left<0?-roi2.Left:0;
   roi1.Left+=Max(temp1,temp2);
   roi2.Left+=Max(temp1,temp2);
   temp1=roi1.Top<0?-roi1.Top:0;
   temp2=roi2.Top<0?-roi2.Top:0;
   roi1.Top+=Max(temp1,temp2);
   roi2.Top+=Max(temp1,temp2);
   temp1=roi1.Right>fr1.XDim-1?roi1.Right-fr1.XDim+1:0;
   temp2=roi2.Right>fr2.XDim-1?roi2.Right-fr2.XDim+1:0;
   roi1.Right-=Max(temp1,temp2);
   roi2.Right-=Max(temp1,temp2);
   temp1=roi1.Bottom>fr1.YDim-1?roi1.Bottom-fr1.YDim+1:0;
   temp2=roi2.Bottom>fr2.YDim-1?roi2.Bottom-fr2.YDim+1:0;
   roi1.Bottom-=Max(temp1,temp2);
   roi2.Bottom-=Max(temp1,temp2);
   float x=0,y=0,xy=0,xx=0,yy=0;
   float xtemp,ytemp;
   for(int i=0;i<=roi1.Height();i++) for (int j=0;j<=roi1.Width();j++)
   {
       xtemp=fr1.ImageData[roi1.Left+j+(i+roi1.Top)*fr1.XDim];
       ytemp=fr2.ImageData[roi2.Left+j+(i+roi2.Top)*fr2.XDim];
       x+=xtemp; y+=ytemp; xy+=xtemp*ytemp;
       xx+=xtemp*xtemp; yy+=ytemp*ytemp;
   }
   int n=(roi1.Height()+1)*(roi1.Width()+1);
   x/=n; y/=n; xy/=n; xx/=n; yy/=n;
   return (xy-x*y)/sqrt((xx-x*x)*(yy-y*y));
}
//------------------------------------------------------
/*int Frame::SaveSPE(AnsiString filename)
{
   TFileStream *File=new TFileStream(filename,fmCreate|fmOpenWrite);
   File->Seek(0,soFromBeginning);
   File->Write(Header,4100);
   long int temp;
   for(int i=0;i<YDim;i++) for(int j=0;j<XDim;j++)
   {
      temp=ImageData[i][j];
      File->Write(&temp,4);
   }
   delete File;
   return 0;
} */
//------------------------------------------------------
