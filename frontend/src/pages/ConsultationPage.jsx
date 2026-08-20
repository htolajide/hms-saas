import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import api from '../services/api';
import { ClipboardList, ArrowLeft, Loader2, Plus, FileText, Pill, TestTube } from 'lucide-react';
import AddConsultationModal from '../components/AddConsultationModal';

export default function ConsultationPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [consultations, setConsultations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [latestTriage, setLatestTriage] = useState(null);
  
  const patientId = new URLSearchParams(location.search).get('patientId');

  // Inside fetchConsultations, also fetch triage:
  const fetchPatientData = async () => {
    try {
      const [consultRes, triageRes] = await Promise.all([
        api.get(`/consultations/patient/${patientId}`),
        api.get(`/triage/patient/${patientId}`)
      ]);
      setConsultations(consultRes.data);
      if (triageRes.data.length > 0) {
        // Get the most recent triage
        setLatestTriage(triageRes.data[triageRes.data.length - 1]); 
      }
    } catch (error) {
      console.error("Failed to fetch data", error);
    } finally {
      setLoading(false);
    }
  };
  

  useEffect(() => {
    if (patientId) {
      fetchPatientData(); 
    }
  }, [patientId]);

  if (!patientId) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <ClipboardList className="h-16 w-16 text-gray-300 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-600">No patient selected</h2>
          <button onClick={() => navigate('/patients')} className="mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
            Go to Patients
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <button onClick={() => navigate(`/triage?patientId=${patientId}`)} className="flex items-center gap-2 text-gray-500 hover:text-primary transition font-medium text-sm">
        <ArrowLeft className="h-4 w-4" /> Back to Triage
      </button>

      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-3xl font-bold text-gray-800 flex items-center gap-3">
            <div className="bg-green-100 p-2 rounded-lg">
              <ClipboardList className="h-7 w-7 text-green-600" />
            </div>
            Consultations
          </h2>
            {/* Triage Badge Header */}
            {latestTriage && (
                <div className="bg-orange-50 border border-orange-200 rounded-2xl p-4 flex flex-wrap items-center gap-6">
                <div className="flex items-center gap-2">
                    <Activity className="h-5 w-5 text-orange-600" />
                    <span className="font-semibold text-orange-800">Latest Triage Vitals:</span>
                </div>
                <div className="flex flex-wrap gap-4 text-sm">
                    <span className="bg-white px-3 py-1 rounded-lg shadow-sm"><strong>BP:</strong> {latestTriage.bloodPressureSystolic}/{latestTriage.bloodPressureDiastolic}</span>
                    <span className="bg-white px-3 py-1 rounded-lg shadow-sm"><strong>Temp:</strong> {latestTriage.temperature}°C</span>
                    <span className="bg-white px-3 py-1 rounded-lg shadow-sm"><strong>Pulse:</strong> {latestTriage.pulseRate} bpm</span>
                    <span className={`px-3 py-1 rounded-lg shadow-sm font-bold ${
                    latestTriage.triageCategory === 'EMERGENCY' ? 'bg-red-100 text-red-800' :
                    latestTriage.triageCategory === 'URGENT' ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'
                    }`}>
                    {latestTriage.triageCategory}
                    </span>
                </div>
                </div>
            )}
          <p className="text-gray-500 mt-2 ml-12">Doctor's notes, diagnosis, and prescriptions</p>
        </div>
        <button onClick={() => setIsModalOpen(true)} className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-lg transition shadow-md font-medium">
          <Plus className="h-5 w-5" /> New Consultation
        </button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center py-20"><Loader2 className="h-8 w-8 text-green-600 animate-spin" /></div>
      ) : consultations.length === 0 ? (
        <div className="bg-white p-12 rounded-2xl text-center text-gray-500">
          <ClipboardList className="h-12 w-12 mx-auto mb-3 text-gray-300" />
          <p className="text-lg font-medium">No consultations yet</p>
        </div>
      ) : (
        <div className="space-y-4">
          {consultations.map((consultation) => (
            <div key={consultation.id} className="bg-white rounded-2xl p-6 border border-gray-200">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <p className="text-sm text-gray-500">Dr. {consultation.doctorName}</p>
                  <p className="text-lg font-semibold text-gray-900">
                    {new Date(consultation.consultationDate).toLocaleString()}
                  </p>
                </div>
                <span className="px-3 py-1 bg-green-100 text-green-800 rounded-full text-xs font-semibold">
                  {consultation.assessment || 'Consultation'}
                </span>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
                <div>
                  <h4 className="text-sm font-semibold text-gray-700 mb-2">Subjective</h4>
                  <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">{consultation.subjective || 'N/A'}</p>
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-gray-700 mb-2">Objective</h4>
                  <p className="text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">{consultation.objective || 'N/A'}</p>
                </div>
              </div>

              <div className="mb-4">
                <h4 className="text-sm font-semibold text-gray-700 mb-2">Assessment & Plan</h4>
                <p className="text-sm text-gray-600 bg-green-50 p-3 rounded-lg">{consultation.assessment || 'N/A'}</p>
                <p className="text-sm text-gray-600 mt-2">{consultation.plan || ''}</p>
              </div>

              {/* Prescriptions */}
              {consultation.prescriptions && consultation.prescriptions.length > 0 && (
                <div className="border-t pt-4 mt-4">
                  <h4 className="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
                    <Pill className="h-4 w-4" /> Prescriptions
                  </h4>
                  <div className="space-y-2">
                    {consultation.prescriptions.map((pres, idx) => (
                      <div key={idx} className="bg-blue-50 p-3 rounded-lg text-sm">
                        <p className="font-semibold text-blue-900">{pres.medicationName} - {pres.dosage}</p>
                        <p className="text-blue-700">{pres.frequency} for {pres.duration}</p>
                        {pres.instructions && <p className="text-blue-600 text-xs mt-1">{pres.instructions}</p>}
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* Lab Orders */}
              {consultation.labOrders && consultation.labOrders.length > 0 && (
                <div className="border-t pt-4 mt-4">
                  <h4 className="text-sm font-semibold text-gray-700 mb-3 flex items-center gap-2">
                    <TestTube className="h-4 w-4" /> Lab Orders
                  </h4>
                  <div className="space-y-2">
                    {consultation.labOrders.map((lab, idx) => (
                      <div key={idx} className="bg-orange-50 p-3 rounded-lg text-sm flex justify-between items-center">
                        <div>
                          <p className="font-semibold text-orange-900">{lab.testName}</p>
                          {lab.notes && <p className="text-orange-700 text-xs">{lab.notes}</p>}
                        </div>
                        <span className={`px-2 py-1 text-xs rounded-full ${
                          lab.status === 'COMPLETED' ? 'bg-green-200 text-green-800' :
                          lab.status === 'PENDING' ? 'bg-yellow-200 text-yellow-800' :
                          'bg-gray-200 text-gray-800'
                        }`}>
                          {lab.status}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {isModalOpen && <AddConsultationModal patientId={patientId} onClose={() => setIsModalOpen(false)} onSaved={fetchConsultations} />}
    </div>
  );
}