//---------------------------------------------------------------------------
#include <vcl.h>
#pragma hdrstop


#include "MovieThread.h"
#include "main.h"
#pragma package(smart_init)
//---------------------------------------------------------------------------
__fastcall MovieThread::MovieThread(int NFrames)
   : TThread(true)
{
   N=NFrames;
   i=MainForm->TrackBar->Position;
   FreeOnTerminate=true;
   MainForm->TrackBar->Enabled=false;
   MainForm->PauseButton->Enabled=true;
   MainForm->PrevButton->Enabled=false;
   MainForm->NextButton->Enabled=false;
   MainForm->ProcessButton->Enabled=false;
   MainForm->FileMenu->Enabled=false;
   MainForm->PrefMenu->Enabled=false;
}
//---------------------------------------------------------------------------
__fastcall MovieThread::~MovieThread()
{
   MainForm->MovThread=NULL;
   MainForm->PauseButton->Enabled=false;
   MainForm->StartButton->Enabled=true;
   MainForm->PrevButton->Enabled=true;
   MainForm->NextButton->Enabled=true;
   MainForm->TrackBar->Enabled=true;
   MainForm->ProcessButton->Enabled=true;
   MainForm->FileMenu->Enabled=true;
   MainForm->PrefMenu->Enabled=true;
   MainForm->MovOn=false;
}
//---------------------------------------------------------------------------
void __fastcall MovieThread::ShowNew()
{
   MainForm->TrackBar->Position=i;
   MainForm->TrackBar->Repaint();
}
//---------------------------------------------------------------------------
void __fastcall MovieThread::Finalize()
{
   MainForm->StartButton->Down=false;
   MainForm->TrackBar->Position=1;
}
//---------------------------------------------------------------------------
void __fastcall MovieThread::Execute()
{
   for(;(i<=N)&&(!Terminated);i++)
   {
      Synchronize(ShowNew);
   }
   if(!Terminated) Synchronize(Finalize);
}
//---------------------------------------------------------------------------
void __fastcall MovieThread::CustomResume()
{
   i=MainForm->TrackBar->Position;
   Resume();
}
//---------------------------------------------------------------------------

