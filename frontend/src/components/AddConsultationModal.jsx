import { useState, useEffect } from 'react';
import api from '../services/api';
import { X, Loader2, Plus, Trash2 } from 'lucide-react';
import { getHospitalId } from '../utils/auth';

export default function AddConsultationModal({ patientId, onClose, onSaved }) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [medications, setMedications] = useState([]);
  const [labTests, setLabTests] = useState([]);
  
  useEffect(() => {
        const fetchCatalogs = async () => {
        try {
            // No hospitalId needed in URL - backend scopes automatically via JWT
            const [medRes, labRes] = await Promise.all([
            api.get('/pharmacy/medications'),
            api.get('/laboratory/tests')
            ]);
            setMedications(medRes.data);
            setLabTests(labRes.data);
        } catch (error) {
            console.error("Failed to load medication/lab catalogs:", error);
        }
        };
        fetchCatalogs();
  }, []);

  const [formData, setFormData] = useState({
    patientId: patientId,
    doctorId: 1, // Get from logged-in user
    hospitalId: 1,
    consultationDate: new Date().toISOString().slice(0, 16),
    subjective: '',
    objective: '',
    assessment: '',
    plan: '',
    notes: '',
    prescriptions: [],
    labOrders: []
  });

  const [newPrescription, setNewPrescription] = useState({
    medicationName: '', dosage: '', frequency: '', duration: '', instructions: '', quantity: 0
  });

  const [newLabOrder, setNewLabOrder] = useState({
    testName: '', testCode: '', notes: ''
  });

  const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });

  const addPrescription = () => {
    if (newPrescription.medicationName) {
      setFormData({ ...formData, prescriptions: [...formData.prescriptions, { ...newPrescription }] });
      setNewPrescription({ medicationName: '', dosage: '', frequency: '', duration: '', instructions: '', quantity: 0 });
    }
  };

  const addLabOrder = () => {
    if (newLabOrder.testName) {
      setFormData({ ...formData, labOrders: [...formData.labOrders, { ...newLabOrder }] });
      setNewLabOrder({ testName: '', testCode: '', notes: '' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true); setError(''); setSuccess('');
    try {
      await api.post('/consultations', formData);
      setSuccess('Consultation saved successfully!');
      onSaved();
      setTimeout(onClose, 1500);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to save consultation');
    } finally { setLoading(false); }
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-4xl max-h-[90vh] overflow-y-auto">
        <div className="flex justify-between items-center p-6 border-b border-gray-100 sticky top-0 bg-white z-10">
          <h3 className="text-xl font-bold text-gray-800">New Consultation</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600"><X className="h-6 w-6" /></button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-6">
          {error && <div className="bg-red-50 text-red-600 p-3 rounded-lg text-sm">{error}</div>}
          {success && <div className="bg-green-50 text-green-700 p-3 rounded-lg text-sm font-medium">{success}</div>}

          {/* SOAP Notes */}
          <div>
            <h4 className="text-sm font-semibold text-gray-500 uppercase mb-3">SOAP Notes</h4>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Subjective (S)</label>
                <textarea name="subjective" value={formData.subjective} onChange={handleChange} rows="2" className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" placeholder="Patient's complaints..." />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Objective (O)</label>
                <textarea name="objective" value={formData.objective} onChange={handleChange} rows="2" className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" placeholder="Physical examination findings..." />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Assessment (A)</label>
                <textarea name="assessment" value={formData.assessment} onChange={handleChange} rows="2" className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" placeholder="Diagnosis..." />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Plan (P)</label>
                <textarea name="plan" value={formData.plan} onChange={handleChange} rows="2" className="w-full px-3 py-2 bg-white text-gray-900 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" placeholder="Treatment plan..." />
              </div>
            </div>
          </div>

          {/* Prescriptions */}
          <div className="border-t pt-4">
            <h4 className="text-sm font-semibold text-gray-500 uppercase mb-3">Prescriptions</h4>
            <div className="grid grid-cols-2 gap-3 mb-3">
                <select 
                    value={newPrescription.medicationName} 
                    onChange={(e) => setNewPrescription({...newPrescription, medicationName: e.target.value})} 
                    className="px-3 py-2 bg-white text-gray-900 border rounded-lg text-sm w-full"
                    >
                    <option value="">Select Medication...</option>
                    {medications.map(med => (
                        <option key={med.id} value={med.name}>{med.name} ({med.category})</option>
                    ))}
                </select>
              <input placeholder="Dosage" value={newPrescription.dosage} onChange={(e) => setNewPrescription({...newPrescription, dosage: e.target.value})} className="px-3 py-2 border bg-white rounded-lg text-sm" />
              <input placeholder="Frequency" value={newPrescription.frequency} onChange={(e) => setNewPrescription({...newPrescription, frequency: e.target.value})} className="px-3 py-2 border bg-white rounded-lg text-sm" />
              <input placeholder="Duration" value={newPrescription.duration} onChange={(e) => setNewPrescription({...newPrescription, duration: e.target.value})} className="px-3 py-2 border bg-white rounded-lg text-sm" />
            </div>
            <button type="button" onClick={addPrescription} className="text-sm text-green-600 hover:text-green-700 flex items-center gap-1"><Plus className="h-4 w-4" /> Add Prescription</button>
            
            {formData.prescriptions.map((p, idx) => (
              <div key={idx} className="bg-blue-50 p-2 rounded-lg mt-2 text-sm flex justify-between">
                <span>{p.medicationName} {p.dosage}</span>
                <button type="button" onClick={() => setFormData({...formData, prescriptions: formData.prescriptions.filter((_, i) => i !== idx)})} className="text-red-600"><Trash2 className="h-4 w-4" /></button>
              </div>
            ))}
          </div>

          {/* Lab Orders */}
          <div className="border-t pt-4">
            <h4 className="text-sm font-semibold text-gray-500 uppercase mb-3">Lab Orders</h4>
            <div className="flex gap-3 mb-3">
                <select 
                    value={newLabOrder.testName} 
                    onChange={(e) => setNewLabOrder({...newLabOrder, testName: e.target.value})} 
                    className="flex-1 px-3 py-2 bg-white text-gray-900 border rounded-lg text-sm"
                    >
                    <option value="">Select Lab Test...</option>
                    {labTests.map(test => (
                        <option key={test.id} value={test.name}>{test.name} ({test.category})</option>
                    ))}
                </select>
                <button type="button" onClick={addLabOrder} className="text-sm text-orange-600 hover:text-orange-700 flex items-center gap-1"><Plus className="h-4 w-4" /> Add</button>
            </div>
            {formData.labOrders.map((l, idx) => (
              <div key={idx} className="bg-orange-50 p-2 rounded-lg mt-2 text-sm flex justify-between">
                <span>{l.testName}</span>
                <button type="button" onClick={() => setFormData({...formData, labOrders: formData.labOrders.filter((_, i) => i !== idx)})} className="text-red-600"><Trash2 className="h-4 w-4" /></button>
              </div>
            ))}
          </div>

          <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
            <button type="button" onClick={onClose} className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200">Cancel</button>
            <button type="submit" disabled={loading} className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 flex items-center gap-2 disabled:opacity-50">
              {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : 'Save Consultation'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}