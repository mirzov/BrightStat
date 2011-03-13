//---------------------------------------------------------------------------

#ifndef MovieThreadH
#define MovieThreadH
//---------------------------------------------------------------------------
#include <Classes.hpp>
//---------------------------------------------------------------------------
class TMainForm;
class MovieThread : public TThread
{
private:
   int i,N;
protected:
   void __fastcall Execute();
public:
   __fastcall MovieThread(int);
   __fastcall ~MovieThread();
   void __fastcall ShowNew();
   void __fastcall Finalize();
   void __fastcall CustomResume();
};
//---------------------------------------------------------------------------
#endif
