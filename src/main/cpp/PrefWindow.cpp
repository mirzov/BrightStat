//---------------------------------------------------------------------------
#include <vcl.h>
#include <fstream.h>
#pragma hdrstop

#include "PrefWindow.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
TPreferForm *PreferForm;
//---------------------------------------------------------------------------
__fastcall TPreferForm::TPreferForm(TComponent* Owner)
   : TForm(Owner)
{
   SetInitial();
}
//---------------------------------------------------------------------------
void TPreferForm::SetInitial()
{
   ROICheckBox->Enabled=false;
   ExCheckBox->Enabled=false;
}
//---------------------------------------------------------------------------
PPars::PPars(void)
{
   ImRad=3;
   SmRad=6;
   BrightNum=8;
   BrightSize=2.24;
   NofStartFrames=1;
   CutOff=0.2;
   NoiseSigms=3;
   Correlation=0.6;
   UseExProfile=false;
   Normalize=false;
   UseROI=false;
   ExFrame=NULL;
}
//---------------------------------------------------------------------------
int PPars::SaveToFile(AnsiString filename)
{
   ofstream file;
   file.open(filename.c_str(),ios::out);
   file<<ImRad<<endl;
   file<<SmRad<<endl;
   file<<BrightNum<<endl;
   file<<NofStartFrames<<endl;
   file<<BrightSize<<endl;
   file<<CutOff<<endl;
   file<<NoiseSigms<<endl;
   file<<Correlation<<endl;
   file.close();
   return 0;
}
//---------------------------------------------------------------------------
int PPars::LoadFromFile(AnsiString filename)
{
   ifstream file;
   file.open(filename.c_str(),ios::in);
   file>>ImRad;
   file>>SmRad;
   file>>BrightNum;
   file>>NofStartFrames;
   file>>BrightSize;
   file>>CutOff;
   file>>NoiseSigms;
   file>>Correlation;
   UseExProfile=false;
   UseROI=false;
   Normalize=false;
   file.close();
   return 0;
}
//---------------------------------------------------------------------------
void TPreferForm::GetPPars(PPars& Pars)
{
   DecimalSeparator='.';
   Pars.ImRad=StrToFloat(ImRadEdit->Text);
   Pars.SmRad=StrToFloat(SmRadEdit->Text);
   Pars.BrightSize=StrToFloat(BrightSizeEdit->Text);
   Pars.BrightNum=StrToInt(BrightNumEdit->Text);
   Pars.NofStartFrames=StrToInt(NofStartFramesEdit->Text);
   Pars.CutOff=StrToFloat(CutoffEdit->Text);
   Pars.NoiseSigms=StrToFloat(SigmasEdit->Text);
   Pars.Correlation=StrToFloat(CorrEdit->Text);
   Pars.UseExProfile=ExCheckBox->Checked;
   Pars.UseROI=ROICheckBox->Checked;
   Pars.Normalize=NormExCheckBox->Checked;
}
//---------------------------------------------------------------------------
void TPreferForm::SetFromPPars(PPars Pars)
{
   DecimalSeparator='.';
   ImRadEdit->Text=FloatToStrF(Pars.ImRad,ffGeneral,4,4);
   SmRadEdit->Text=FloatToStrF(Pars.SmRad,ffGeneral,4,4);
   BrightSizeEdit->Text=FloatToStrF(Pars.BrightSize,ffGeneral,4,4);
   BrightNumEdit->Text=(AnsiString)Pars.BrightNum;
   NofStartFramesEdit->Text=(AnsiString)Pars.NofStartFrames;
   CutoffEdit->Text=FloatToStrF(Pars.CutOff,ffGeneral,4,4);
   SigmasEdit->Text=FloatToStrF(Pars.NoiseSigms,ffGeneral,4,4);
   CorrEdit->Text=FloatToStrF(Pars.Correlation,ffGeneral,4,4);
   ROICheckBox->Enabled=Pars.UseROI;
   ROICheckBox->Checked=Pars.UseROI;
   ExCheckBox->Checked=Pars.UseExProfile;
   NormExCheckBox->Checked=Pars.Normalize;
   NormExCheckBox->Enabled=Pars.UseExProfile;
   CutoffEdit->Enabled=Pars.UseExProfile;
}
//---------------------------------------------------------------------------
void __fastcall TPreferForm::ExCheckBoxClick(TObject *Sender)
{
   CutoffEdit->Enabled=ExCheckBox->Checked;
   NormExCheckBox->Enabled=ExCheckBox->Checked;
   NormExCheckBox->Checked=ExCheckBox->Checked;
}
//---------------------------------------------------------------------------

