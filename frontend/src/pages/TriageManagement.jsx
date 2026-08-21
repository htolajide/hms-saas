import { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import api from '../services/api';
import { Activity, ArrowLeft, Loader2, Search, Thermometer, Droplet, Heart, Wind, Scale, Ruler } from 'lucide-react';
import AddTriageModal from '../components/AddTriageModal';
import { getHospitalId } from '../utils/auth';

export default function TriageManagement() {
  const navigate = useNavigate();
  const location = useLocation();
  const [triageRecords, setTriageRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  
  // Get patientId from URL if navigating from patient list
  const patientId = new URLSearchParams(location.search).get('patientId');

  const fetchTriage = async () => {
    try {
      const response = await api.get(`/triage/patient/${patientId}`);
      setTriageRecords(response.data);
    } catch (error) {
      console.error("Failed to fetch triage records", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (patientId) {
      fetchTriage();
    }
  }, [patientId]);

  if (!patientId) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <Activity className="h-16 w-16 text-gray-300 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-600">No patient selected</h2>
          <p className="text-gray-500 mt-2">Please select a patient from the patient list</p>
          <button onClick={() => navigate('/patients')} className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700">
            Go to Patients
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <button onClick={() => navigate('/patients')} className="flex items-center gap-2 text-gray-500 hover:text-primary transition font-medium text-sm">
        <ArrowLeft className="h-4 w-4" /> Back to Patients
      </button>

      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-3xl font-bold text-gray-800 flex items-center gap-3">
            <div className="bg-orange-100 p-2 rounded-lg">
              <Activity className="h-7 w-7 text-orange-600" />
            </div>
            Triage Records
          </h2>
          <p className="text-gray-500 mt-2 ml-12">Patient vitals and chief complaints</p>
        </div>
        <button onClick={() => setIsModalOpen(true)} className="flex items-center gap-2 bg-orange-600 hover:bg-orange-700 text-white px-6 py-3 rounded-lg transition shadow-md font-medium">
          <Activity className="h-5 w-5" /> Record Vitals
        </button>
      </div>

      {loading ? (
        <div className="flex justify-center items-center py-20"><Loader2 className="h-8 w-8 text-orange-600 animate-spin" /></div>
      ) : triageRecords.length === 0 ? (
        <div className="bg-white p-12 rounded-2xl text-center text-gray-500">
          <Activity className="h-12 w-12 mx-auto mb-3 text-gray-300" />
          <p className="text-lg font-medium">No triage records yet</p>
          <p className="text-sm mt-1">Record the patient's vitals to get started</p>
        </div>
      ) : (
        <div className="bg-white rounded-2xl overflow-hidden">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-6">
            {triageRecords.map((record) => (
              <div key={record.id} className="border border-gray-200 rounded-xl p-4 hover:shadow-md transition">
                <div className="flex justify-between items-start mb-3">
                  <div>
                    <p className="text-sm text-gray-500">{record.patientId}</p>
                    <p className="font-semibold text-gray-900">{record.patientName}</p>
                  </div>
                  <span className={`px-2 py-1 text-xs font-semibold rounded-full ${
                    record.triageCategory === 'EMERGENCY' ? 'bg-red-100 text-red-800' :
                    record.triageCategory === 'URGENT' ? 'bg-orange-100 text-orange-800' :
                    'bg-green-100 text-green-800'
                  }`}>
                    {record.triageCategory || 'N/A'}
                  </span>
                </div>
                
                <div className="space-y-2 text-sm">
                  <div className="flex items-center gap-2">
                    <Thermometer className="h-4 w-4 text-gray-400" />
                    <span className="text-gray-600">Temp:</span>
                    <span className="font-medium">{record.temperature}°C</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Droplet className="h-4 w-4 text-gray-400" />
                    <span className="text-gray-600">BP:</span>
                    <span className="font-medium">{record.bloodPressureSystolic}/{record.bloodPressureDiastolic} mmHg</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Heart className="h-4 w-4 text-gray-400" />
                    <span className="text-gray-600">Pulse:</span>
                    <span className="font-medium">{record.pulseRate} bpm</span>
                  </div>
                  {record.bmi && (
                    <div className="flex items-center gap-2">
                      <Scale className="h-4 w-4 text-gray-400" />
                      <span className="text-gray-600">BMI:</span>
                      <span className="font-medium">{record.bmi}</span>
                    </div>
                  )}
                </div>

                <div className="mt-3 pt-3 border-t border-gray-100">
                  <p className="text-xs text-gray-500 font-semibold mb-1">Chief Complaint:</p>
                  <p className="text-sm text-gray-700">{record.chiefComplaint || 'N/A'}</p>
                </div>

                <div className="mt-2 text-xs text-gray-400">
                  {new Date(record.createdAt).toLocaleString()}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {isModalOpen && <AddTriageModal patientId={patientId} onClose={() => setIsModalOpen(false)} onSaved={fetchTriage} />}
    </div>
  );
}