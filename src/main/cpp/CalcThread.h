//---------------------------------------------------------------------------
#ifndef CalcThreadH
#define CalcThreadH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include "FrameSeq.h"
#include "Molecules.h"
#include "PrefWindow.h"
//---------------------------------------------------------------------------
class CalcThread : public TThread
{
private:
   MolecList* RefList;
   int StartFrame,f;
   void __fastcall NextFrame();
//   void __fastcall ProcessMovie();
   void AddExEmSignals();
protected:
   void __fastcall Execute();
public:
   __fastcall CalcThread();
   __fastcall ~CalcThread();
   void __fastcall Finalize();
   int i,N;
};
//---------------------------------------------------------------------------
#endif
