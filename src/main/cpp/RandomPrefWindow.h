//---------------------------------------------------------------------------

#ifndef RandomPrefWindowH
#define RandomPrefWindowH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
//---------------------------------------------------------------------------
class TRandomPreferForm : public TForm
{
__published:	// IDE-managed Components
   TLabel *Label1;
   TEdit *NMolEdit;
   TLabel *Label2;
   TEdit *BMolEdit;
   TLabel *Label3;
   TEdit *BDistrEdit;
   TLabel *Label4;
   TEdit *DiamMolEdit;
   TLabel *Label5;
   TEdit *OffsetEdit;
   TLabel *Label6;
   TEdit *OffsetNoiseEdit;
   TLabel *Label7;
   TEdit *PoisFactEdit;
   TLabel *Label8;
   TEdit *NMol2Edit;
   TLabel *Label9;
   TEdit *BMol2Edit;
   TLabel *Label10;
   TEdit *BDistr2Edit;
   TLabel *Label11;
   TEdit *BackEdit;
   TButton *OkButton;
   TButton *CancelButton;
private:	// User declarations
public:		// User declarations
   __fastcall TRandomPreferForm(TComponent* Owner);
};
//---------------------------------------------------------------------------
struct RandomPPars
{
   int Nmols,Nmols2;
   float B,Bdist,B2,Bdist2;
   float Mdiam;
   float Offset,OffsetNoise;
   float PoissFactor;
   float BackLevel;
};
//---------------------------------------------------------------------------
extern PACKAGE TRandomPreferForm *RandomPreferForm;
//---------------------------------------------------------------------------
#endif
