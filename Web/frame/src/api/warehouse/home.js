import { get } from '../../utils/http';

export const getInitDataApi = p => get('/wHome/getInitData', p);
export const getSmartAnalysisApi = p => get('/smart/restock', p);
