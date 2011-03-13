//---------------------------------------------------------------------------
#include <Forms.hpp>
#include <vcl.h>
#pragma hdrstop

#include "CalcThread.h"
#include "Algorithms.h"
#include "main.h"
#pragma package(smart_init)
//---------------------------------------------------------------------------
__fastcall CalcThread::CalcThread():TThread(true)
{
   FreeOnTerminate=true;

   MainForm->ProcessButton->Caption="Stop!";
   MainForm->FileMenu->Enabled=false;
   MainForm->PrefMenu->Enabled=false;
   MainForm->StartButton->Enabled=false;
   MainForm->PrevButton->Enabled=false;
   MainForm->NextButton->Enabled=false;
   MainForm->TrackBar->Enabled=false;
}
//---------------------------------------------------------------------------
__fastcall CalcThread::~CalcThread()
{
   MainForm->ProcessButton->Caption="Process \"movie\"";
   MainForm->FileMenu->Enabled=true;
   MainForm->PrefMenu->Enabled=true;
   MainForm->StartButton->Enabled=true;
   MainForm->PrevButton->Enabled=true;
   MainForm->NextButton->Enabled=true;
   MainForm->TrackBar->Enabled=true;
   MainForm->CalOn=false;
}
//---------------------------------------------------------------------------
void __fastcall CalcThread::Execute()
{
   for(int movN = 0; !Terminated && movN < MainForm->OpenDialog->Files->Count; movN++)
   {
      if(movN > 0) MainForm->LoadMovie(movN);
      int x,y,i;
      MolecList *StartList=new MolecList(500); //deleting will be done from ListArray.~MolecListArray()
      DetectMoleculesFromScratch(MainForm->Movie,MainForm->Pars,StartList);
      if(StartList->Count == 0){
        Application->MessageBox("No molecules detected!", "Problem!");
      }else{
        StartFrame=MainForm->Movie.GetFrameNumber();
        MainForm->Results=MolecListArray(MainForm->Movie.GetNFrames());
        RefList=new MolecList(StartList);
        MainForm->Results.AddList(StartList,MainForm->Movie.GetFrameNumber()-1);
        AddExEmSignals(); //to avoid missing the signals for the StartFrame

        MolecList* savedRefList = new MolecList(RefList);
        for(f=StartFrame-1;f>StartFrame - MainForm->Pars.NofStartFrames && !Terminated ;f--)
        {
           Synchronize(NextFrame);
        }
        delete RefList;
        RefList = savedRefList;
        for(f=StartFrame+1;f<=MainForm->Movie.GetNFrames() && !Terminated ;f++)
        {
           Synchronize(NextFrame);
        }
        if (f>MainForm->Movie.GetNFrames()) Synchronize(Finalize);

        if(!Terminated){
           AddExEmSignals();
           MainForm->ListResults();   //RESULTS OUTPUT
        }
        delete RefList;
      }
   }
}
//---------------------------------------------------------------------------
void CalcThread::AddExEmSignals()
{
   AddSignalValues(*(MainForm->Movie.GetCurFrame()),
                        MainForm->ExSignals,MainForm->EmSignals,
                        MainForm->Pars,MainForm->Movie.GetFrameNumber()-1);
}
//---------------------------------------------------------------------------
//void __fastcall CalcThread::ProcessMovie()
//{
//}
//---------------------------------------------------------------------------
void __fastcall CalcThread::NextFrame()
{
   if(Terminated) return;
   MainForm->TrackBar->Position=f;
   MainForm->Update();
   int NMols=MainForm->Results[StartFrame-1]->Count;
   MolecList* CurList=new MolecList(NMols);//will be deleted from MainForm->Results.~MolecListArray()
   FollowMolecules(*MainForm->Movie.GetCurFrame(),RefList,MainForm->Pars,CurList); //THE CALCULATION ITSELF
   AddSignalValues(*MainForm->Movie.GetCurFrame(),MainForm->ExSignals,MainForm->EmSignals,MainForm->Pars,f-1);
   MainForm->Results.AddList(CurList,f-1);
}
//---------------------------------------------------------------------------
void __fastcall CalcThread::Finalize()
{
   MainForm->TrackBar->Position=StartFrame - MainForm->Pars.NofStartFrames + 1;
   MainForm->Update();
}
//---------------------------------------------------------------------------

