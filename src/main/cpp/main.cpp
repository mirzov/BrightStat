//---------------------------------------------------------------------------
#include <vcl.h>
#include <Graphics.hpp>
#include <math.h>
#include <Math.hpp>
#include <algorith.h>
#include <fstream.h>
#pragma hdrstop

#include "main.h"
#include "Algorithms.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"

TMainForm *MainForm;
//---------------------------------------------------------------------------
__fastcall TMainForm::TMainForm(TComponent* Owner)
   : TForm(Owner)
{
   Bitmap=new Graphics::TBitmap;
   CurDir=GetCurrentDir();
   if(FileExists(CurDir+"\\config.txt")) Pars.LoadFromFile(CurDir+"\\config.txt");
   CalOn=false;
   MovOn=false;
   ExSignals=new float[1]; EmSignals=new float[1];
}
//---------------------------------------------------------------------------
__fastcall TMainForm::~TMainForm()
{
   delete Bitmap;
   delete[] ExSignals; delete[] EmSignals;
   Pars.SaveToFile(CurDir+"\\config.txt");
}
//---------------------------------------------------------------------------
void TMainForm::LoadMovie(int movieNumber)
{
   FName=(*OpenDialog->Files)[movieNumber];
   switch(Movie.Load(FName)){
     case 1:{
             Application->MessageBoxA("Could not open file for exclusive reading. Please check that it is not occupied.", "Problem!");
             break;
     }
     case 2:{
             Application->MessageBoxA("The file has FLOATING POINT data type, which is not supported.", "Problem!");
             break;
     }
     case 0:{
        Caption="BrightStat - "+ExtractFileName(FName);
        int x=Movie.GetXDim(),y=Movie.GetYDim(),n=Movie.GetNFrames();
        DimEdit->Text=IntToStr(x)+" x "+IntToStr(y)+" x "+IntToStr(n);
        Constraints->MinHeight=Max(290,y+175);
        Constraints->MinWidth=Max(240,x+180);
        TrackBar->Max=Movie.GetNFrames();
        TrackBar->Enabled=true;
        if(TrackBar->Position!=1) TrackBar->Position=1;
        else Movie.LoadFrame(1);
        DrawBitmap(*Movie.GetCurFrame());
        DrawImage();
        ProcessButton->Enabled=true;
        SaveMarksMenu->Enabled=true;
        LoadExMenu->Enabled=true;
        StartButton->Enabled=true;
        PrevButton->Enabled=true;
        NextButton->Enabled=true;
        PreferForm->SetInitial();
        delete[] ExSignals; delete[] EmSignals;
        ExSignals=new float[Movie.GetNFrames()];EmSignals=new float[Movie.GetNFrames()];
     }
   }
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::OpenMenuClick(TObject *Sender)
{
   Caption="BrightStat";
   DimEdit->Text="1 x 1 x 1";
   if(OpenDialog->Execute())
   {
        LoadMovie(0);
   }
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::ProcessButtonClick(TObject *Sender)
{
   if(!CalOn)
   {
        CalThread=new CalcThread();
        CalOn=true;
        CalThread->Resume();
   }else
   {
      CalThread->Terminate();
      CalThread->WaitFor();
   }
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::LoadExMenuClick(TObject *Sender)
{
   if(OpenDialog->Execute())
   {
      ExFrame.LoadFromFile(OpenDialog->FileName);
      PreferForm->ExCheckBox->Enabled=Pars.UseExProfile;
      Pars.UseExProfile=true;
      Pars.Normalize=true;
      Pars.ExFrame=&ExFrame;
      LoadExMenu->Enabled=false;
   }
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::ExitMenuClick(TObject *Sender)
{
   Close();
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::FormPaint(TObject *Sender)
{
   DrawImage();
}
//---------------------------------------------------------------------------
void TMainForm::DrawBitmap(Frame& ImFrame)
{
   int YDim=ImFrame.GetYDim();
   int XDim=ImFrame.GetXDim();
   TRect rect=TRect(0,0,XDim,YDim);
   DrawParams DPars;
   DPars.Color=Green;
   DPars.Rect=rect;
   DPars.MaxSignal=ImFrame.GetMax();
   Bitmap->Width=XDim;
   Bitmap->Height=YDim;
   ImFrame.DrawCanvas(Bitmap->Canvas,DPars);
}
//---------------------------------------------------------------------------
void TMainForm::DrawImage()
{
   TRect rect=TRect(1,1,Bevel->Width-1,Bevel->Height-1);
   Canvas->StretchDraw(rect,Bitmap);
}
//---------------------------------------------------------------------------
void TMainForm::ListResults()
{
   AnsiString FileName=ExtractFileName(FName),OutFile;
   FileName=FileName.SubString(1,FileName.Length()-4);
   AnsiString DirName=ExtractFilePath(FName)+FileName;
   CreateDir(DirName);
   SetCurrentDir(DirName);
   fstream file;
   int NMols,NFrames=0,i,j,n,x,y;
   float *MolIntegrInt,tmp;
   OutFile=DirName+"\\Kinetics.txt";
   file.open(OutFile.c_str(),ios::out);
   for(n=0;n<Results.GetN() && !Results[n];n++){};
   int StartFrame=n;
   if(Pars.UseExProfile)
   {          //deleting the molecules outside Pars.Cutoff*ExFrame.GetMax() area;
      for(i=0;i<Results[n]->Count;i++)
      {
         x=Results[StartFrame]->Mol(i).x;
         y=Results[StartFrame]->Mol(i).y;
         if(ExFrame[y][x]<Pars.CutOff*ExFrame.GetMax())
            for(j=0;j<Results.GetN();j++) if(Results[j]) Results[j]->Delete(i);
      }
   }
   NMols=Results[StartFrame]->Count; //the first processed frame gives the number of molecules
   file<<"Frame\t";
   for(i=0;i<NMols;i++) file<<"Mol"<<i+1<<"\t";//kinetics file header
   file<<endl;
   MolIntegrInt=new float[NMols];
   setmem(MolIntegrInt,NMols*sizeof(float),0);//starting integral values are zeros
   for(;n<Results.GetN() && Results[n];n++)//printing kinetics file and counting aver.int.
   {
      NFrames++;  //counting actual number of frames
      file<<n+1<<"\t";
      for(i=0;i<NMols;i++)
      {
         x=Results[StartFrame]->Mol(i).x;
         y=Results[StartFrame]->Mol(i).y;
         tmp=Results[n]->Mol(i).I;
         if(Pars.Normalize) tmp*=(float)ExFrame.GetMax()/ExFrame[y][x];
         MolIntegrInt[i]+=tmp; //integral intensity of each molecule
         file<<floor(tmp)<<"\t";
      }
      file<<endl;
   }
   file.close();
   OutFile=DirName+"\\"+FileName+"_coor.txt";
   file.open(OutFile.c_str(),ios::out);
   file<<"MolNum\tX\tY\tIaver";
   if(Pars.UseExProfile) file<<"\tIex"; file<<endl;  //coordinate file header
   for(i=0;i<NMols;i++)
   {
      x=Results[StartFrame]->Mol(i).x;
      y=Results[StartFrame]->Mol(i).y;
      file<<i+1<<"\t"<<x+1<<"\t"<<y+1<<"\t"<<floor(MolIntegrInt[i]/NFrames); //aver. int.
      if(Pars.UseExProfile) file<<"\t"<<(float)ExFrame[y][x]/ExFrame.GetMax(); file<<endl;
   }
   file.close();
   //ImproveSignals(ExSignals,NFrames);
   //ImproveSignals(EmSignals,NFrames);
   OutFile=DirName+"\\"+"SignalsEx.txt";
   file.open(OutFile.c_str(),ios::out);
   for(i=0;i<NFrames;i++) file<<ExSignals[i]<<endl;
   file.close();
   OutFile=DirName+"\\"+"SignalsEm.txt";
   file.open(OutFile.c_str(),ios::out);
   for(i=0;i<NFrames;i++) file<<EmSignals[i]<<endl;
   file.close();
   delete[] MolIntegrInt;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::PrefMenuClick(TObject *Sender)
{
   PreferForm->SetFromPPars(Pars);
   if(PreferForm->ShowModal()==mrOk) PreferForm->GetPPars(Pars);
   ChooseBevel->Visible=Pars.UseROI;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::BevelMouseDown(TObject *Sender,
      TMouseButton Button, TShiftState Shift, int X, int Y)
{
//   if(MovieThread) return;
   ChooseBevel->Visible=false;
   if(Button!=mbLeft) return;
   rect=TRect(X,Y,X,Y);
   SetROI();
   ChooseBevel->Visible=true;
   ChooseBevel->Tag=1; //the ROI is being chosen
   Pars.UseROI=true;
//   DrawBitmap();
//   DrawImage();
}
//---------------------------------------------------------------------------
void TMainForm::SetROI()
{
   float xc=(rect.left+rect.right)*.5, yc=(rect.top+rect.bottom)*.5;
   float ra=(rect.right-rect.left)*.5, rb=(rect.bottom-rect.top)*.5;
   float l=xc-ra*1.41421, r=xc+ra*1.41421;  //smallest area ellipse
   float t=yc-rb*1.41421, b=yc+rb*1.41421;  //drawn around the rectangle
   ChooseBevel->Left=ceil(l);
   ChooseBevel->Width=floor(r-l);
   ChooseBevel->Top=ceil(t);
   ChooseBevel->Height=floor(b-t);
   float xfactor=(float)Movie.GetXDim()/(Bevel->ClientWidth-1);
   float yfactor=(float)Movie.GetYDim()/(Bevel->ClientHeight-1);
   Pars.roiLeft=ceil(l*xfactor);
   Pars.roiRight=floor(r*xfactor);
   Pars.roiTop=ceil(t*yfactor);
   Pars.roiBottom=floor(b*yfactor);
   LeftTopEdit->Text=(AnsiString)Pars.roiLeft+","+(AnsiString)Pars.roiTop;
   RightBottomEdit->Text=(AnsiString)Pars.roiRight+","+(AnsiString)Pars.roiBottom;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::BevelMouseMove(TObject *Sender,
      TShiftState Shift, int X, int Y)
{
   if(!ChooseBevel->Tag) return;
   if( (X>=Bevel->ClientWidth-1)||(X<=1)||(Y>=Bevel->ClientHeight-1)||(Y<=1) )
   {
      ChooseBevel->Visible=false;
      ChooseBevel->Tag=0;
      return;
   }
   rect.Right=X; rect.Bottom=Y;
   SetROI();
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::BevelMouseUp(TObject *Sender,
      TMouseButton Button, TShiftState Shift, int X, int Y)
{
//   if(!ChooseBevel->Tag) return;
   ChooseBevel->Tag=0;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::StartButtonClick(TObject *Sender)
{
   if(!MovOn)
   {
      MovThread=new MovieThread(Movie.GetNFrames());
      MovOn=true;
      MovThread->Resume();
   }else
   {
      MovThread->Terminate();
      if(MovThread->Suspended) MovThread->Resume();
      MovThread->WaitFor();
   }
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::PauseButtonClick(TObject *Sender)
{
   if(!MovThread->Suspended)
   {
      TrackBar->Enabled=true;
      PrevButton->Enabled=true;
      NextButton->Enabled=true;
      MovThread->Suspend();
   }else
   {
      PrevButton->Enabled=false;
      NextButton->Enabled=false;
      TrackBar->Enabled=false;
      MovThread->CustomResume();
   }
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::PrevButtonClick(TObject *Sender)
{
   if(TrackBar->Position>TrackBar->Min) TrackBar->Position--;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::NextButtonClick(TObject *Sender)
{
   if(TrackBar->Position<TrackBar->Max) TrackBar->Position++;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::TrackBarChange(TObject *Sender)
{
   CurEdit->Text=(AnsiString)TrackBar->Position;
   Movie.LoadFrame(TrackBar->Position);
   DrawBitmap(*Movie.GetCurFrame());
   DrawImage();
   if(Pars.UseROI) ChooseBevel->Repaint();
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::FormResize(TObject *Sender)
{
   float xfactor=(float)(Bevel->ClientWidth-1)/Movie.GetXDim();
   float yfactor=(float)(Bevel->ClientHeight-1)/Movie.GetYDim();
   int l=Pars.roiLeft*xfactor;
   int r=Pars.roiRight*xfactor;
   int t=Pars.roiTop*yfactor;
   int b=Pars.roiBottom*yfactor;
   ChooseBevel->Left=l;
   ChooseBevel->Width=r-l;
   ChooseBevel->Top=t;
   ChooseBevel->Height=b-t;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::SaveMarksMenuClick(TObject *Sender)
{
   if(SaveDialog->Execute())
   {
      Frame Marks=GetMarksFrame(*Movie.GetCurFrame(),Pars);
      Marks.SaveToFile(SaveDialog->FileName,Movie.GetHeader());
   }
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::ExRoiButtonClick(TObject *Sender)
{
   Pars.ExRoiLeft=Pars.roiLeft;
   Pars.ExRoiRight=Pars.roiRight;
   Pars.ExRoiTop=Pars.roiTop;
   Pars.ExRoiBottom=Pars.roiBottom;
}
//---------------------------------------------------------------------------
void __fastcall TMainForm::EmRoiButtonClick(TObject *Sender)
{
   Pars.EmRoiLeft=Pars.roiLeft;
   Pars.EmRoiRight=Pars.roiRight;
   Pars.EmRoiTop=Pars.roiTop;
   Pars.EmRoiBottom=Pars.roiBottom;
}
//---------------------------------------------------------------------------

