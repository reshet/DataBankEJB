package com.mplatforma.amr.service.remote;

import com.mresearch.databank.shared.OrgDTO;
import com.mresearch.databank.shared.ResearchFilesDTO;
import com.mresearch.databank.shared.SocioResearchDTO;
import com.mresearch.databank.shared.UserAccountDTO;
import javax.ejb.Remote;

import com.mresearch.databank.shared.VarDTO_Detailed;
import java.util.ArrayList;

@Remote
public interface AdminSocioResearchBeanRemote {
      Boolean deleteResearch(long id);
      SocioResearchDTO updateResearch(SocioResearchDTO research);
      SocioResearchDTO updateResearchGrouped(SocioResearchDTO research);

      VarDTO_Detailed generalizeVar(long var_id, ArrayList<Long> gen_var_ids,UserAccountDTO dto);

      long parseSPSS(long blobkey, long length);

      long addOrgImpl(OrgDTO dto);

      Boolean addFileToAccessor(long id_research,long id_file,String desc,String category);
      Boolean deleteFileFromAccessor(long id_research,long id_file);
      Boolean addSSE(String clas,String kind,String value);
      Boolean updateFileAccessor(long research_id,ResearchFilesDTO dto);
}
