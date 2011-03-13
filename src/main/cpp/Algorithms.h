#ifndef AlgorithmsH
#define AlgorithmsH

#include "Molecules.h"
#include "FrameSeq.h"
#include "PrefWindow.h"
#include "CalcThread.h"
//--------------------------------------------------
struct Statistics
{
   float Mean,StDev;
   Statistics():Mean(0),StDev(0){};
};
//--------------------------------------------------
void DetectMoleculesFromScratch(FrameSeq&,PPars&,MolecList*);
Frame GetMarksFrame(Frame&,PPars&);
void FollowMolecules(Frame&,MolecList*,PPars&,MolecList*);
void AddSignalValues(Frame&,float*,float*,PPars&,int);
void DetectMolsAndMaxs(Frame&,Frame&,MolecList*,PPars&,MolecList*);
void MarkBrightNonMolecules(Frame&,Frame&,MolecList*,PPars&);
void CalcSignals(Frame&,Frame&,MolecList*,PPars&,bool);
bool CheckIfMolecule(Frame&,int,int,PPars&);
bool CheckIfInROI(int,int,PPars&);
void AddNextBrightest(Frame&,MolecList*);
Statistics CalcLocStat(Frame&,Frame&,int,int,PPars&);
void ImproveSignals(float*,int);
//void CalcExInt(MolecList*,Frame&,PPars&);
//MolecListArray TraceMolecules(FrameSeq&,PPars&);
//--------------------------------------------------

#endif
