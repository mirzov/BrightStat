//---------------------------------------------------------------------------
#ifndef PrefWindowH
#define PrefWindowH
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>

#include "FrameSeq.h"
//---------------------------------------------------------------------------
class PPars
{
public:
   float ImRad,SmRad,BrightSize,CutOff,NoiseSigms,Correlation;
   int BrightNum,NofStartFrames;
   int roiLeft,roiRight,roiTop,roiBottom;
   int ExRoiLeft,ExRoiRight,ExRoiTop,ExRoiBottom;
   int EmRoiLeft,EmRoiRight,EmRoiTop,EmRoiBottom;
   bool UseExProfile,Normalize,UseROI;
   Frame* ExFrame;
   PPars(void);
   ~PPars(){};
   int SaveToFile(AnsiString);
   int LoadFromFile(AnsiString);
};
//---------------------------------------------------------------------------
class TPreferForm : public TForm
{
__published:	// IDE-managed Components
   TLabel *Label1;
   TLabel *Label2;
   TLabel *Label3;
   TButton *OkButton;
   TButton *CancelButton;
   TEdit *ImRadEdit;
   TEdit *SmRadEdit;
   TEdit *BrightNumEdit;
   TLabel *Label4;
   TEdit *CutoffEdit;
   TLabel *Label6;
   TEdit *BrightSizeEdit;
   TLabel *Label7;
   TEdit *SigmasEdit;
   TCheckBox *ExCheckBox;
   TCheckBox *NormExCheckBox;
   TCheckBox *ROICheckBox;
   TLabel *Label5;
   TLabel *Label8;
   TEdit *CorrEdit;
   TEdit *NofStartFramesEdit;
   TLabel *Label9;
   void __fastcall ExCheckBoxClick(TObject *Sender);
private:	// User declarations
public:		// User declarations
   __fastcall TPreferForm(TComponent* Owner);
   void SetFromPPars(PPars);
   void GetPPars(PPars&);
   void SetInitial();
};
//---------------------------------------------------------------------------
extern PACKAGE TPreferForm *PreferForm;
//---------------------------------------------------------------------------
#endif
