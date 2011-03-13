#ifndef FrameSeqH
#define FrameSeqH

#define MaxDim 10000
#define MaxFrames 100000

#include <System.hpp>
#include <Classes.hpp>
#include <Graphics.hpp>
#include <fstream.h>
//--------------------------------------------------
//typedef Windows::TRect WRect;
enum BasicColor{Red,Green,Blue};
struct DrawParams
{
   BasicColor Color;
   float MaxSignal;
   TRect Rect;
};
//--------------------------------------------------
class FrameSeq;
class Frame
{
public:
   Frame();
   Frame(int,int);
   Frame(const Frame&);
   ~Frame();
   int* operator[](int);
   void CalcParams();
   bool CheckIfLocalMax(int,int);
   Frame& operator=(const Frame&);
   Frame& operator-=(const Frame&);
   int GetXDim(){return XDim;};
   int GetYDim(){return YDim;};
   int GetMean(){return MeanValue;};
   int GetMax(){return MaxValue;};
   int LoadFromFile(AnsiString);
   int Load(TFileStream*,short int);
   int SaveToFile(AnsiString,void*);
   void DrawCanvas(TCanvas*,DrawParams&);
   void ShiftToLocalMax(int&,int&);
   void Reset(); //sets values to zero, keeps the dimensions
private:
   int XDim,YDim;
   int *ImageData;
   int MaxValue,MinValue,MeanValue;
   friend class FrameSeq;
   friend float FrameCorr(Frame&,Frame&,TRect,TRect);
};
//--------------------------------------------------
float FrameCorr(Frame&,Frame&,TRect,TRect);
//--------------------------------------------------
class FrameSeq
{
   public:
      FrameSeq();
      ~FrameSeq();
      int GetNFrames(){return NFrames;};
      int GetXDim(){return XDim;};
      int GetYDim(){return YDim;};
      int Load(AnsiString);
      int LoadFrame(int);//the first is 1, not 0
//      int SaveFrameToSPE(AnsiString,int,int);
      Frame* GetCurFrame(){return CurFrame;};
      void* GetHeader(){return Header;};
      int GetFrameNumber(){return FrameNumber;};
   private:
      void FreeFile();
      TFileStream* File;
      short int DatLength;
      void *Header;
      int NFrames,FrameNumber;
      int XDim, YDim;
      Frame *CurFrame;
};
//--------------------------------------------------
#endif
