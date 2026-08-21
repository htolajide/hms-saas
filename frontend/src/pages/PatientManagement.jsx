import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { HeartPulse, Plus, Loader2, Search, ArrowLeft, Edit, Activity, ClipboardList } from 'lucide-react';
import AddPatientModal from '../components/AddPatientModal';

export default function PatientManagement() {
  const navigate = useNavigate();
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  // Hardcoded for now, later we will get this from the logged-in user's profile

  const fetchPatients = async () => {
    try {
        const response = await api.get('/patients'); 
        setPatients(response.data);
    } catch (error) {
      console.error("Failed to fetch patients", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, []);

  const filteredPatients = patients.filter(p => 
    p.fullName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    p.patientId.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-gray-500 hover:text-primary transition font-medium text-sm">
        <ArrowLeft className="h-4 w-4" /> Back to Dashboard
      </button>

      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h2 className="text-3xl font-bold text-gray-800 flex items-center gap-3">
            <div className="bg-red-100 p-2 rounded-lg">
              <HeartPulse className="h-7 w-7 text-red-600" />
            </div>
            Patient Management
          </h2>
          <p className="text-gray-500 mt-2 ml-12">Register patients and manage medical records.</p>
        </div>
        <button onClick={() => setIsModalOpen(true)} className="flex items-center gap-2 bg-red-600 hover:bg-red-700 text-white px-6 py-3 rounded-lg transition shadow-md font-medium">
          <Plus className="h-5 w-5" /> Register Patient
        </button>
      </div>

      <div className="relative">
        <Search className="absolute left-4 top-3.5 h-5 w-5 text-gray-400" />
        <input type="text" placeholder="Search by name or Patient ID..." value={searchTerm} onChange={(e) => setSearchTerm(e.target.value)} className="w-full pl-12 pr-4 py-3 bg-white border border-gray-200 rounded-xl focus:ring-2 focus:ring-red-500 focus:border-transparent outline-none shadow-sm" />
      </div>

      {loading ? (
        <div className="flex justify-center items-center py-20"><Loader2 className="h-8 w-8 text-red-600 animate-spin" /></div>
      ) : filteredPatients.length === 0 ? (
        <div className="bg-white p-12 rounded-2xl text-center text-gray-500">
          <HeartPulse className="h-12 w-12 mx-auto mb-3 text-gray-300" />
          <p className="text-lg font-medium">No patients found.</p>
        </div>
      ) : (
        <div className="bg-white rounded-2xl overflow-hidden">
          <div className="hidden md:block overflow-x-auto">
            <table className="w-full text-left">
              <thead className="bg-gray-50/50">
                <tr>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Patient ID</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Full Name</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Gender / Age</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Blood Group</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider">Phone</th>
                  <th className="px-6 py-4 text-xs font-semibold text-gray-500 uppercase tracking-wider text-center">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {filteredPatients.map((p) => (
                  <tr key={p.id} className="hover:bg-gray-50/50 transition">
                    <td className="px-6 py-4 text-sm font-bold text-red-600">{p.patientId}</td>
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">{p.fullName}</td>
                    <td className="px-6 py-4 text-sm text-gray-600">{p.gender}</td>
                    <td className="px-6 py-4">
                      <span className="px-3 py-1 text-xs font-semibold rounded-full bg-red-100 text-red-800">{p.bloodGroup || 'N/A'}</span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">{p.phone}</td>
                    <td className="px-6 py-4 text-center">
                    <div className="flex justify-center gap-2">
                        <button onClick={() => navigate(`/triage?patientId=${p.id}`)} className="p-2 text-orange-600 hover:bg-orange-50 rounded-lg transition" title="Triage">
                        <Activity className="h-4 w-4" />
                        </button>
                        <button onClick={() => navigate(`/consultations?patientId=${p.id}`)} className="p-2 text-green-600 hover:bg-green-50 rounded-lg transition" title="Consultations">
                        <ClipboardList className="h-4 w-4" />
                        </button>
                    </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {isModalOpen && <AddPatientModal onClose={() => setIsModalOpen(false)} onSaved={fetchPatients} />}
    </div>
  );
}